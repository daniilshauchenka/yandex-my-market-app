package ru.yandex.practicum.paymentservice.service;

import java.util.List;
import ru.yandex.practicum.paymentservice.dto.ItemDto;
import ru.yandex.practicum.paymentservice.dto.PagingDto;
import ru.yandex.practicum.paymentservice.enums.SortType;

public interface ItemService {

  List<List<ItemDto>> getItems(String search, SortType sortType, int pageNumber, int pageSize);

  PagingDto getPaging(String search, SortType sortType, int pageNumber, int pageSize);

  ItemDto getItem(Long id);
}
