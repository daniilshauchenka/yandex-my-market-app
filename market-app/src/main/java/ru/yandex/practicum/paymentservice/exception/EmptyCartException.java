package ru.yandex.practicum.paymentservice.exception;

public class EmptyCartException extends MarketException {

  public EmptyCartException() {
    super(ErrorCode.EMPTY_CART);
  }
}
