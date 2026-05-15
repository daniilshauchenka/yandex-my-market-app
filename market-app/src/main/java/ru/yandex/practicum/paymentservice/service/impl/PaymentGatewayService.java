package ru.yandex.practicum.paymentservice.service.impl;


import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.client.api.DefaultApi;
import ru.yandex.practicum.payment.client.model.BalanceResponse;
import ru.yandex.practicum.payment.client.model.PaymentRequest;
import ru.yandex.practicum.payment.client.model.PaymentResponse;

@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final DefaultApi paymentApi;

    public Mono<Boolean> pay(BigDecimal amount) {

        PaymentRequest request = new PaymentRequest();
        request.setAmount(amount);

        return paymentApi.makePayment(request)
            .map(PaymentResponse::getSuccess);
    }

    public Mono<BigDecimal> getBalance() {

        return paymentApi.getBalance()
            .map(BalanceResponse::getBalance);
    }
}