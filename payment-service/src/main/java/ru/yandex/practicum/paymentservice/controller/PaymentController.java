package ru.yandex.practicum.paymentservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import ru.yandex.practicum.payment.api.DefaultApi;
import ru.yandex.practicum.payment.model.BalanceResponse;
import ru.yandex.practicum.payment.model.PaymentRequest;
import ru.yandex.practicum.payment.model.PaymentResponse;
import ru.yandex.practicum.paymentservice.service.PaymentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class PaymentController implements DefaultApi {

    private final PaymentService paymentService;

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(ServerWebExchange exchange) {
        return paymentService.getBalance().map(balance -> {
            BalanceResponse response = new BalanceResponse();
            response.setBalance(balance);
            return ResponseEntity.ok(response);
        });
    }

    @Override
    public Mono<ResponseEntity<PaymentResponse>> makePayment(
            Mono<PaymentRequest> paymentRequestMono, ServerWebExchange exchange) {
        return paymentRequestMono
                .flatMap(request -> paymentService.makePayment(request.getAmount()))
                .map(ResponseEntity::ok);
    }
}
