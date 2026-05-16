package ru.yandex.practicum.paymentservice.exception;

public class PaymentFailedException extends MarketException {

    public PaymentFailedException() {
        super(ErrorCode.PAYMENT_FAILED);
    }
}
