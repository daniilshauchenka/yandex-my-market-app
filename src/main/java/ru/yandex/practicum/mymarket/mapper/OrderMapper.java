package ru.yandex.practicum.mymarket.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.mymarket.config.GlobalMapperConfig;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.entity.Order;

@Mapper(config = GlobalMapperConfig.class)
public interface OrderMapper {

  @Mapping(target = "items", source = "items")
  OrderDto toDto(Order order);

  List<OrderDto> toDtoList(List<Order> orders);
}
