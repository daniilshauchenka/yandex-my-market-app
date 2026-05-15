package ru.yandex.practicum.mymarket.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.mymarket.config.GlobalMapperConfig;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.entity.Order;
import ru.yandex.practicum.mymarket.entity.OrderItem;

@Mapper(config = GlobalMapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "items", source = "items")
    OrderDto toDto(Order order, List<OrderItem> items);
}
