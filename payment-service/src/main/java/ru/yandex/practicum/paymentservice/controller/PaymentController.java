package ru.yandex.practicum.paymentservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.paymentservice.service.PaymentService;
import ru.yandex.practicum.payment.api.DefaultApi;
import ru.yandex.practicum.payment.model.BalanceResponse;
import ru.yandex.practicum.payment.model.PaymentRequest;
import ru.yandex.practicum.payment.model.PaymentResponse;

@RestController
@RequiredArgsConstructor
public class PaymentController implements DefaultApi {

    private final PaymentService paymentService;


    public Mono<ResponseEntity<BalanceResponse>> getBalance() {
        return paymentService.getBalance()
            .map(balance -> {
                BalanceResponse response = new BalanceResponse();
                response.setBalance(balance);
                return ResponseEntity.ok(response);
            });
    }


    public Mono<ResponseEntity<PaymentResponse>> pay(
        Mono<PaymentRequest> paymentRequestMono) {

        return paymentRequestMono
            .flatMap(request ->
                paymentService.pay(request.getAmount()))
            .map(success -> {
                PaymentResponse response = new PaymentResponse();
                response.setSuccess(success);
                return ResponseEntity.ok(response);
            });
    }
}