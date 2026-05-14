package ru.yandex.practicum.mymarket.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.entity.Order;
import ru.yandex.practicum.mymarket.entity.OrderItem;
import ru.yandex.practicum.mymarket.exception.EmptyCartException;
import ru.yandex.practicum.mymarket.exception.ItemNotFoundException;
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
        return orderRepository.findAllByOrderByIdDesc()
            .collectList()
            .flatMap(orders -> {
                if (orders.isEmpty()) {
                    return Mono.just(Collections.emptyList());
                }
                List<Long> orderIds = orders.stream()
                    .map(Order::getId)
                    .toList();
                return orderItemRepository.findAllByOrderIdIn(orderIds)
                    .collectMultimap(OrderItem::getOrderId)
                    .map(itemsByOrderId ->
                        orders.stream()
                            .map(order ->
                                orderMapper.toDto(
                                    order,
                                    new ArrayList<>(
                                        itemsByOrderId.getOrDefault(
                                            order.getId(),
                                            List.of()
                                        )
                                    )
                                )
                            )
                            .toList()
                    );
            });
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
                    return loadItems(cartItems)
                        .flatMap(itemsById -> {
                            BigDecimal total = calculateTotal(cartItems, itemsById);
                            Order order = Order.builder().totalSum(total).build();
                            return orderRepository
                                .save(order)
                                .flatMap(savedOrder ->
                                    saveOrderItems(savedOrder, cartItems, itemsById)
                                        .then(cartItemRepository.deleteAll())
                                        .thenReturn(savedOrder.getId()));
                        });
                });
    }

    private Mono<Map<Long, Item>> loadItems(List<CartItem> cartItems) {
        List<Long> itemIds = cartItems.stream().map(CartItem::getItemId).toList();
        return itemRepository.findAllById(itemIds).collectMap(Item::getId);
    }

    private BigDecimal calculateTotal(List<CartItem> cartItems, Map<Long, Item> itemsById) {
        return cartItems.stream()
            .map(cartItem -> {
                Item item = itemsById.get(cartItem.getItemId());
                if (item == null) {
                    throw new ItemNotFoundException();
                }
                return item.getPrice().multiply(BigDecimal.valueOf(cartItem.getCount()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Mono<Void> saveOrderItems(
        Order order, List<CartItem> cartItems, Map<Long, Item> itemsById) {
        return Flux.fromIterable(cartItems)
            .map(cartItem -> {
                Item item = itemsById.get(cartItem.getItemId());
                if (item == null) {
                    throw new ItemNotFoundException();
                }
                return OrderItem.builder()
                    .orderId(order.getId())
                    .itemId(item.getId())
                    .title(item.getTitle())
                    .price(item.getPrice())
                    .count(cartItem.getCount())
                    .build();
            })
            .as(orderItemRepository::saveAll)
            .then();
    }

    private Mono<OrderDto> toDto(Order order) {
        return orderItemRepository
            .findAllByOrderId(order.getId())
            .collectList()
            .map(items -> orderMapper.toDto(order, items));
    }
}
