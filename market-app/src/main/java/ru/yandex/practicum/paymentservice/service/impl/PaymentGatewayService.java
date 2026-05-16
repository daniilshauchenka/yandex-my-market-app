package ru.yandex.practicum.paymentservice.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.payment.client.api.DefaultApi;
import ru.yandex.practicum.payment.client.model.BalanceResponse;
import ru.yandex.practicum.payment.client.model.PaymentRequest;
import ru.yandex.practicum.payment.client.model.PaymentResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final DefaultApi paymentApi;

    public boolean pay(BigDecimal amount) {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(amount);
        return Boolean.TRUE.equals(
                paymentApi.makePayment(request).map(PaymentResponse::getSuccess).block());
    }

    public BigDecimal getBalance() {
        return paymentApi.getBalance().map(BalanceResponse::getBalance).block();
    }

    public boolean isAvailable() {
        try {
            return getBalance() != null;
        } catch (Exception ex) {
            return false;
        }
    }
}
