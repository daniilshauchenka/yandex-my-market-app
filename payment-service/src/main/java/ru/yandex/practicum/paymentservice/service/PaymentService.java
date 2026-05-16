package ru.yandex.practicum.paymentservice.service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.payment.model.PaymentResponse;

import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    private final AtomicReference<BigDecimal> balance = new AtomicReference<>(BigDecimal.valueOf(10000));

    public Mono<BigDecimal> getBalance() {
        return Mono.just(balance.get());
    }

    public Mono<PaymentResponse> makePayment(BigDecimal amount) {
        BigDecimal current = balance.get();
        PaymentResponse response = new PaymentResponse();
        if (current.compareTo(amount) < 0) {
            response.setSuccess(false);
            response.setBalance(current);
            return Mono.just(response);
        }
        BigDecimal updated = current.subtract(amount);
        balance.set(updated);
        response.setSuccess(true);
        response.setBalance(updated);
        return Mono.just(response);
    }
}
