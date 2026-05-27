package ru.yandex.practicum.paymentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.paymentservice.config.PaymentProperties;
import ru.yandex.practicum.payment.model.PaymentResponse;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(new PaymentProperties(BigDecimal.valueOf(10000)));
    }

    @Test
    void shouldReturnInitialBalance() {
        BigDecimal balance = paymentService.getBalance("buyer").block();
        assertEquals(BigDecimal.valueOf(10000), balance);
    }

    @Test
    void shouldDecreaseBalanceAfterPayment() {
        PaymentResponse response =
                paymentService.makePayment("buyer", BigDecimal.valueOf(1000)).block();
        assertTrue(response.getSuccess());
        assertEquals(BigDecimal.valueOf(9000), response.getBalance());
    }

    @Test
    void shouldFailPaymentWhenNotEnoughMoney() {
        PaymentResponse response =
                paymentService.makePayment("buyer", BigDecimal.valueOf(20000)).block();
        assertFalse(response.getSuccess());
        assertEquals(BigDecimal.valueOf(10000), response.getBalance());
    }
}
