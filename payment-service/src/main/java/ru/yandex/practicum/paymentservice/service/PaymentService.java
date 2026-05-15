package ru.yandex.practicum.paymentservice.service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    private final AtomicReference<BigDecimal> balance =
        new AtomicReference<>(BigDecimal.valueOf(10000));

    public Mono<BigDecimal> getBalance() {
        return Mono.just(balance.get());
    }

    public synchronized Mono<Boolean> pay(BigDecimal amount) {

        BigDecimal current = balance.get();

        if (current.compareTo(amount) < 0) {
            return Mono.just(false);
        }

        balance.set(current.subtract(amount));

        return Mono.just(true);
    }
}