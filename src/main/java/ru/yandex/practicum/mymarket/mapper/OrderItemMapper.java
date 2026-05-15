package ru.yandex.practicum.mymarket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.mymarket.config.GlobalMapperConfig;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.entity.OrderItem;

@Mapper(config = GlobalMapperConfig.class)
public interface OrderItemMapper {

    @Mapping(target = "id", source = "itemId")
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "imgPath", ignore = true)
    ItemDto toDto(OrderItem orderItem);
}
