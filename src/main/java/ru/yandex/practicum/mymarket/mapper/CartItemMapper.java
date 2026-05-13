package ru.yandex.practicum.mymarket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.mymarket.config.GlobalMapperConfig;
import ru.yandex.practicum.mymarket.dto.CartItemDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Item;

@Mapper(config = GlobalMapperConfig.class)
public interface CartItemMapper {

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "title", source = "item.title")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "imgPath", source = "item.imgPath")
    @Mapping(target = "price", source = "item.price")
    @Mapping(target = "count", source = "cartItem.count")
    CartItemDto toDto(CartItem cartItem, Item item);
}
