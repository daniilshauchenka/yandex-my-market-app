package ru.yandex.practicum.mymarket.exception;

public class EmptyCartException extends MarketException {

    public EmptyCartException() {
        super(ErrorCode.EMPTY_CART);
    }
}
