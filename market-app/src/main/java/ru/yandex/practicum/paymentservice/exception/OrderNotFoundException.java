package ru.yandex.practicum.paymentservice.exception;

public class OrderNotFoundException extends MarketException {

    public OrderNotFoundException() {
        super(ErrorCode.ORDER_NOT_FOUND);
    }
}
