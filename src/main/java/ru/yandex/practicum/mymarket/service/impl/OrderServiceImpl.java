package ru.yandex.practicum.mymarket.service.impl;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Order;
import ru.yandex.practicum.mymarket.entity.OrderItem;
import ru.yandex.practicum.mymarket.exception.EmptyCartException;
import ru.yandex.practicum.mymarket.exception.OrderNotFoundException;
import ru.yandex.practicum.mymarket.mapper.OrderMapper;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;
import ru.yandex.practicum.mymarket.service.OrderService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;

    @Override
    public Mono<List<OrderDto>> getOrders() {
        return orderRepository.findAllByOrderByIdDesc().flatMap(this::toDto).collectList();
    }

    @Override
    public Mono<OrderDto> getOrder(Long id) {
        return orderRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new OrderNotFoundException()))
                .flatMap(this::toDto);
    }

    @Override
    @Transactional
    public Mono<Long> buy() {
        return cartItemRepository
                .findAllByOrderByIdAsc()
                .collectList()
                .flatMap(
                        cartItems -> {
                            if (cartItems.isEmpty()) {
                                return Mono.error(new EmptyCartException());
                            }
                            return calculateTotal(cartItems)
                                    .flatMap(
                                            total -> {
                                                Order order =
                                                        Order.builder().totalSum(total).build();
                                                return orderRepository
                                                        .save(order)
                                                        .flatMap(
                                                                savedOrder ->
                                                                        saveOrderItems(
                                                                                        savedOrder,
                                                                                        cartItems)
                                                                                .then(
                                                                                        cartItemRepository
                                                                                                .deleteAll())
                                                                                .thenReturn(
                                                                                        savedOrder
                                                                                                .getId()));
                                            });
                        });
    }

    private Mono<OrderDto> toDto(Order order) {
        return orderItemRepository
                .findAllByOrderId(order.getId())
                .collectList()
                .map(items -> orderMapper.toDto(order, items));
    }

    private Mono<BigDecimal> calculateTotal(List<CartItem> cartItems) {
        return Flux.fromIterable(cartItems)
                .flatMap(
                        cartItem ->
                                itemRepository
                                        .findById(cartItem.getItemId())
                                        .map(
                                                item ->
                                                        item.getPrice()
                                                                .multiply(
                                                                        BigDecimal.valueOf(
                                                                                cartItem
                                                                                        .getCount()))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Mono<Void> saveOrderItems(Order order, List<CartItem> cartItems) {
        return Flux.fromIterable(cartItems)
                .flatMap(
                        cartItem ->
                                itemRepository
                                        .findById(cartItem.getItemId())
                                        .flatMap(
                                                item -> {
                                                    OrderItem orderItem =
                                                            OrderItem.builder()
                                                                    .orderId(order.getId())
                                                                    .itemId(item.getId())
                                                                    .title(item.getTitle())
                                                                    .price(item.getPrice())
                                                                    .count(cartItem.getCount())
                                                                    .build();
                                                    return orderItemRepository.save(orderItem);
                                                }))
                .then();
    }
}
