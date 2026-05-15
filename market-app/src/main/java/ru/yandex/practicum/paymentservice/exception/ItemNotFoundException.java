package ru.yandex.practicum.paymentservice.exception;

public class ItemNotFoundException extends MarketException {

  public ItemNotFoundException() {
    super(ErrorCode.ITEM_NOT_FOUND);
  }
}
