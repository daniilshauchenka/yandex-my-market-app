package ru.yandex.practicum.mymarket.dto;

import ru.yandex.practicum.mymarket.enums.Action;

public record ChangeCartItemRequest(Long id, Action action) {}
