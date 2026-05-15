package ru.yandex.practicum.paymentservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum SortType {
  NO(Sort.unsorted()),

  ALPHA(Sort.by("title").ascending()),

  PRICE(Sort.by("price").ascending());

  private final Sort sort;
}
