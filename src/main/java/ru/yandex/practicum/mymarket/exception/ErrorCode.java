package ru.yandex.practicum.mymarket.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  ITEM_NOT_FOUND("error.item.not-found"),

  ORDER_NOT_FOUND("error.order.not-found"),

  VALIDATION_ERROR("error.validation"),

  INVALID_PAGE_SIZE("error.invalid-page-size"),

  EMPTY_CART("error.empty-cart"),

  INTERNAL_ERROR("error.internal");

  private final String messageKey;
}
