package ru.yandex.practicum.mymarket.service.impl;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Order;
import ru.yandex.practicum.mymarket.entity.OrderItem;
import ru.yandex.practicum.mymarket.exception.EmptyCartException;
import ru.yandex.practicum.mymarket.exception.OrderNotFoundException;
import ru.yandex.practicum.mymarket.mapper.OrderMapper;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;
import ru.yandex.practicum.mymarket.service.OrderService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderDto> getOrders() {
        return orderMapper.toDtoList(orderRepository.findAllByOrderByIdDesc());
    }

    @Override
    public OrderDto getOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(OrderNotFoundException::new);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public Long buy() {
        List<CartItem> cartItems = cartItemRepository.findAllByOrderByIdAsc();

        if (cartItems.isEmpty()) {
            throw new EmptyCartException();
        }
        BigDecimal totalSum = cartItems.stream()
            .map(cartItem -> cartItem.getItem()
                .getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getCount())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = Order.builder()
            .totalSum(totalSum)
            .build();
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