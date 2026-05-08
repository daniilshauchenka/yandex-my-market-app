package ru.yandex.practicum.mymarket.dto;

public record CartItemDto(
    Long id,
    ItemDto item,
    Integer count
) {

}