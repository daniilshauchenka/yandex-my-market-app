package ru.yandex.practicum.paymentservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import ru.yandex.practicum.paymentservice.exception.PaymentFailedException;
import ru.yandex.practicum.paymentservice.service.impl.PaymentGatewayService;

@SpringBootTest
class OrderServicePaymentTest {

    @MockitoBean
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    private OrderService orderService;

    @Test
    void shouldThrowExceptionWhenPaymentFails() {
        when(paymentGatewayService.pay(any())).thenReturn(false);
        assertThrows(PaymentFailedException.class, () -> orderService.buy());
    }
}
