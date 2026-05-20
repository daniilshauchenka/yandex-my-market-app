package ru.yandex.practicum.paymentservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.yandex.practicum.paymentservice.dto.OrderDto;
import ru.yandex.practicum.paymentservice.entity.CartItem;
import ru.yandex.practicum.paymentservice.entity.Order;
import ru.yandex.practicum.paymentservice.exception.EmptyCartException;
import ru.yandex.practicum.paymentservice.exception.OrderNotFoundException;
import ru.yandex.practicum.paymentservice.mapper.OrderMapper;
import ru.yandex.practicum.paymentservice.repository.CartItemRepository;
import ru.yandex.practicum.paymentservice.repository.OrderRepository;
import ru.yandex.practicum.paymentservice.service.impl.OrderServiceImpl;
import ru.yandex.practicum.paymentservice.util.TestData;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldReturnOrders() {

        List<Order> orders = List.of(TestData.order());

        List<OrderDto> expected = List.of(TestData.orderDto());

        when(orderRepository.findAllByOrderByIdDesc()).thenReturn(orders);

        when(orderMapper.toDtoList(orders)).thenReturn(expected);

        List<OrderDto> actual = orderService.getOrders();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnOrder() {

        Order order = TestData.order();

        OrderDto dto = TestData.orderDto();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        when(orderMapper.toDto(order)).thenReturn(dto);

        OrderDto actual = orderService.getOrder(1L);

        assertThat(actual).isEqualTo(dto);
    }

    @Test
    void shouldThrowWhenOrderNotFound() {

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(1L)).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void shouldThrowWhenCartIsEmpty() {

        when(cartItemRepository.findAllByOrderByIdAsc()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> orderService.buy()).isInstanceOf(EmptyCartException.class);
    }

    @Test
    void shouldCreateOrder() {

        CartItem cartItem = TestData.cartItem(BigDecimal.valueOf(100), 2);

        Order savedOrder = TestData.order();

        savedOrder.setId(1L);

        when(cartItemRepository.findAllByOrderByIdAsc()).thenReturn(List.of(cartItem));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Long orderId = orderService.buy();

        assertThat(orderId).isEqualTo(1L);

        verify(orderRepository).save(any(Order.class));

        verify(cartItemRepository).deleteAllInBatch(anyList());
    }
}
