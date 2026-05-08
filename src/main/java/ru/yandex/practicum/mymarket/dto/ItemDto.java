package ru.yandex.practicum.mymarket.dto;

import java.math.BigDecimal;

public record ItemDto(
    long id,
    String title,
    String description,
    String imgPath,
    BigDecimal price
) {

}