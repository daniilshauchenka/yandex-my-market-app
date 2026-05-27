package ru.yandex.practicum.paymentservice.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.payment.model.PaymentResponse;
import ru.yandex.practicum.paymentservice.config.PaymentProperties;

import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    private final Map<String, AtomicReference<BigDecimal>> balances = new ConcurrentHashMap<>();
    private final PaymentProperties paymentProperties;

    public PaymentService(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    private AtomicReference<BigDecimal> balanceFor(String username) {
        return balances.computeIfAbsent(username, u -> new AtomicReference<>(paymentProperties.initialBalance()));
    }

    public Mono<BigDecimal> getBalance(String username) {
        return Mono.just(balanceFor(username).get());
    }

    public Mono<PaymentResponse> makePayment(String username, BigDecimal amount) {
        AtomicReference<BigDecimal> balance = balanceFor(username);
        while (true) {
            BigDecimal current = balance.get();
            PaymentResponse response = new PaymentResponse();
            if (current.compareTo(amount) < 0) {
                response.setSuccess(false);
                response.setBalance(current);
                return Mono.just(response);
            }
            BigDecimal updated = current.subtract(amount);
            if (balance.compareAndSet(current, updated)) {
                response.setSuccess(true);
                response.setBalance(updated);
                return Mono.just(response);
            }
        }
    }
}
