package ru.yandex.practicum.mymarket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.entity.Order;
import ru.yandex.practicum.mymarket.entity.OrderItem;
import ru.yandex.practicum.mymarket.exception.EmptyCartException;
import ru.yandex.practicum.mymarket.exception.OrderNotFoundException;
import ru.yandex.practicum.mymarket.mapper.OrderMapper;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;
import ru.yandex.practicum.mymarket.service.impl.OrderServiceImpl;
import ru.yandex.practicum.mymarket.util.TestData;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldReturnOrders() {
        Order order = TestData.order();
        OrderDto dto = TestData.orderDto();
        OrderItem orderItem = TestData.orderItem();
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(Flux.just(orderItem));
        when(orderRepository.findAllByOrderByIdDesc()).thenReturn(Flux.just(order));
        when(orderMapper.toDto(order, List.of(orderItem))).thenReturn(dto);
        StepVerifier.create(orderService.getOrders())
            .assertNext(actual ->
                assertThat(actual).containsExactly(dto))
            .verifyComplete();
    }

    @Test
    void shouldReturnOrder() {
        Order order = TestData.order();
        OrderItem orderItem = TestData.orderItem();
        OrderDto dto = TestData.orderDto();
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(Flux.just(orderItem));
        when(orderRepository.findById(1L)).thenReturn(Mono.just(order));
        when(orderMapper.toDto(order, List.of(orderItem))).thenReturn(dto);
        StepVerifier.create(orderService.getOrder(1L))
            .assertNext(actual ->
                assertThat(actual).isEqualTo(dto))
            .verifyComplete();
    }

    @Test
    void shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById(1L))
            .thenReturn(Mono.empty());
        StepVerifier.create(orderService.getOrder(1L))
            .expectError(OrderNotFoundException.class)
            .verify();
    }

    @Test
    void shouldThrowWhenCartIsEmpty() {
        when(cartItemRepository.findAllByOrderByIdAsc())
            .thenReturn(Flux.empty());
        StepVerifier.create(orderService.buy())
            .expectError(EmptyCartException.class)
            .verify();
    }

    @Test
    void shouldCreateOrder() {
        CartItem cartItem = TestData.cartItem(1L, 1L, 2);
        Order savedOrder = TestData.order();
        Item item = TestData.item(1L, BigDecimal.valueOf(100));
        savedOrder.setId(1L);
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item));
        when(cartItemRepository.findAllByOrderByIdAsc()).thenReturn(Flux.just(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(savedOrder));
        when(cartItemRepository.deleteAll()).thenReturn(Mono.empty());
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(Mono.just(TestData.orderItem()));
        StepVerifier.create(orderService.buy())
            .assertNext(orderId ->
                assertThat(orderId).isEqualTo(1L))
            .verifyComplete();
        verify(orderRepository).save(any(Order.class));
    }
}