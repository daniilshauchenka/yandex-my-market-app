package ru.yandex.practicum.mymarket.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.mymarket.config.GlobalMapperConfig;
import ru.yandex.practicum.mymarket.dto.CartItemDto;
import ru.yandex.practicum.mymarket.entity.CartItem;

@Mapper(config = GlobalMapperConfig.class)
public interface CartItemMapper {

  @Mapping(target = "id", source = "item.id")
  @Mapping(target = "title", source = "item.title")
  @Mapping(target = "description", source = "item.description")
  @Mapping(target = "imgPath", source = "item.imgPath")
  @Mapping(target = "price", source = "item.price")
  @Mapping(target = "count", source = "count")
  CartItemDto toDto(CartItem cartItem);

  List<CartItemDto> toDtoList(List<CartItem> cartItems);
}
