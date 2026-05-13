package ru.yandex.practicum.mymarket.service;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.ItemsPageDto;
import ru.yandex.practicum.mymarket.dto.PagingDto;
import ru.yandex.practicum.mymarket.enums.SortType;

public interface ItemService {

    Mono<ItemsPageDto> getItemsPage(String search, SortType sortType, int pageNumber, int pageSize);

    Mono<PagingDto> getPaging(String search, SortType sortType, int pageNumber, int pageSize);

    Mono<ItemDto> getItem(Long id);
}
