package ru.yandex.practicum.mymarket.service;

import java.util.List;
import ru.yandex.practicum.mymarket.dto.ItemDto;

public interface ItemService {

    ItemDto getById(Long id);

    List<ItemDto> getAll();
}