package ru.yandex.practicum.mymarket.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.mymarket.config.GlobalMapperConfig;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.entity.Item;

@Mapper(config = GlobalMapperConfig.class)
public interface ItemMapper {

    ItemDto toDto(Item item);

    List<ItemDto> toDtoList(List<Item> items);
}