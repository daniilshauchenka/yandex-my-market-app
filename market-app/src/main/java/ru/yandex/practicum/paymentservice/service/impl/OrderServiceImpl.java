package ru.yandex.practicum.paymentservice.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.paymentservice.config.security.CurrentUserService;
import ru.yandex.practicum.paymentservice.dto.OrderDto;
import ru.yandex.practicum.paymentservice.entity.CartItem;
import ru.yandex.practicum.paymentservice.entity.Order;
import ru.yandex.practicum.paymentservice.entity.OrderItem;
import ru.yandex.practicum.paymentservice.exception.EmptyCartException;
import ru.yandex.practicum.paymentservice.exception.OrderNotFoundException;
import ru.yandex.practicum.paymentservice.exception.PaymentFailedException;
import ru.yandex.practicum.paymentservice.mapper.OrderMapper;
import ru.yandex.practicum.paymentservice.repository.CartItemRepository;
import ru.yandex.practicum.paymentservice.repository.OrderRepository;
import ru.yandex.practicum.paymentservice.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final PaymentGatewayService paymentGatewayService;
    private final CurrentUserService currentUserService;

    @Override
    public List<OrderDto> getOrders() {
        Long userId = currentUserService.requireCurrentUser().getId();
        return orderMapper.toDtoList(orderRepository.findAllByUserIdOrderByIdDesc(userId));
    }

    @Override
    public OrderDto getOrder(Long id) {
        Long userId = currentUserService.requireCurrentUser().getId();
        Order order = orderRepository.findByIdAndUserId(id, userId).orElseThrow(OrderNotFoundException::new);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public Long buy() {
        var user = currentUserService.requireCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findAllByUserIdOrderByIdAsc(user.getId());

        if (cartItems.isEmpty()) {
            throw new EmptyCartException();
        }
        BigDecimal totalSum = cartItems.stream()
                .map(cartItem -> cartItem.getItem().getPrice().multiply(BigDecimal.valueOf(cartItem.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean paymentSuccess = paymentGatewayService.pay(totalSum);
        if (!paymentSuccess) {
            throw new PaymentFailedException();
        }

        Order order = Order.builder().user(user).totalSum(totalSum).build();
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .title(cartItem.getItem().getTitle())
                        .price(cartItem.getItem().getPrice())
                        .count(cartItem.getCount())
                        .itemId(cartItem.getItem().getId())
                        .build())
                .toList();
        order.getItems().addAll(orderItems);
        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteAllInBatch(cartItems);
        return savedOrder.getId();
    }
}
