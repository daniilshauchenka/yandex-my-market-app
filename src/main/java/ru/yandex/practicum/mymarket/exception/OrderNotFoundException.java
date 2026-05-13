package ru.yandex.practicum.mymarket.exception;

public class OrderNotFoundException extends MarketException {

  public OrderNotFoundException() {
    super(ErrorCode.ORDER_NOT_FOUND);
  }
}
