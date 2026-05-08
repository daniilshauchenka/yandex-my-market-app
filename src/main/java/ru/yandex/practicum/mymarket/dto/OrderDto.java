package ru.yandex.practicum.mymarket.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(
    long id,
    List<ItemDto> items,
    BigDecimal totalSum
) {

}