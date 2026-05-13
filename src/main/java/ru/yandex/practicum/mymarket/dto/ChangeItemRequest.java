package ru.yandex.practicum.mymarket.dto;

import ru.yandex.practicum.mymarket.enums.Action;
import ru.yandex.practicum.mymarket.enums.SortType;

public record ChangeItemRequest(
        Long id,
        Action action,
        String search,
        SortType sort,
        Integer pageNumber,
        Integer pageSize) {}
