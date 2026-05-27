package ru.yandex.practicum.paymentservice.service.impl;

import java.math.BigDecimal;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.payment.client.api.DefaultApi;
import ru.yandex.practicum.payment.client.model.BalanceResponse;
import ru.yandex.practicum.payment.client.model.PaymentRequest;
import ru.yandex.practicum.payment.client.model.PaymentResponse;
import ru.yandex.practicum.paymentservice.config.security.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final DefaultApi paymentApi;
    private final CurrentUserService currentUserService;

    public boolean pay(BigDecimal amount) {
        String username = currentUserService.requireCurrentUser().getUsername();
        PaymentRequest request = new PaymentRequest();
        request.setAmount(amount);
        return Boolean.TRUE.equals(paymentApi
                .makePayment(username, request)
                .map(PaymentResponse::getSuccess)
                .block());
    }

    public BigDecimal getBalance() {
        String username = currentUserService.requireCurrentUser().getUsername();
        return paymentApi.getBalance(username).map(BalanceResponse::getBalance).block();
    }

    @Nullable
    public BigDecimal tryGetBalance() {
        try {
            return getBalance();
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean isAvailable() {
        return tryGetBalance() != null;
    }
}
