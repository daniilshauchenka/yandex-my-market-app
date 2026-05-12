package ru.yandex.practicum.mymarket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.mymarket.config.GlobalMapperConfig;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.entity.Item;

@Mapper(config = GlobalMapperConfig.class)
public interface ItemMapper {

  @Mapping(target = "count", source = "count")
  ItemDto toDto(Item item, Integer count);

  default ItemDto toDto(Item item) {
    return toDto(item, 0);
  }
}
