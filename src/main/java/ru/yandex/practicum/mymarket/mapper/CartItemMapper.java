package ru.yandex.practicum.mymarket.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import ru.yandex.practicum.mymarket.config.GlobalMapperConfig;
import ru.yandex.practicum.mymarket.dto.CartItemDto;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Item;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = ItemMapper.class
)
public interface CartItemMapper {

    CartItemDto toDto(CartItem cartItem);

    List<CartItemDto> toDtoList(List<CartItem> cartItems);
}