package ru.yandex.practicum.paymentservice.enums;

import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
    NO(Sort.unsorted()),

    ALPHA(Sort.by("title").ascending()),

    PRICE(Sort.by("price").ascending());

    private final Sort sort;
}
