package ru.yandex.practicum.mymarket.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import ru.yandex.practicum.mymarket.config.GlobalMapperConfig;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.entity.OrderItem;

@Mapper(config = GlobalMapperConfig.class)
public interface OrderItemMapper {

    default ItemDto toItemDto(
        OrderItem orderItem
    ) {

        return new ItemDto(
            orderItem.getItemId(),
            orderItem.getTitle(),
            null,
            null,
            orderItem.getPrice(),
            orderItem.getCount()
        );
    }

    List<ItemDto> toItemDtoList(
        List<OrderItem> orderItems
    );
}