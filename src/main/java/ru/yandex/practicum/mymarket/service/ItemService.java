package ru.yandex.practicum.mymarket.service;

import java.util.List;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.PagingDto;
import ru.yandex.practicum.mymarket.enums.SortType;

public interface ItemService {

    List<List<ItemDto>> getItems(
        String search,
        SortType sortType,
        int pageNumber,
        int pageSize
    );

    PagingDto getPaging(
        String search,
        SortType sortType,
        int pageNumber,
        int pageSize
    );

    ItemDto getItem(Long id);
}