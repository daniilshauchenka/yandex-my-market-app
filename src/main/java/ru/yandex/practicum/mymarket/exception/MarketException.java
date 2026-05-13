package ru.yandex.practicum.mymarket.exception;

import lombok.Getter;

@Getter
public class MarketException extends RuntimeException {

  private final ErrorCode errorCode;

  public MarketException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }
}
