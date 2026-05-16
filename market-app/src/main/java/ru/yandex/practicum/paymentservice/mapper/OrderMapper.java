package ru.yandex.practicum.paymentservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.yandex.practicum.paymentservice.config.GlobalMapperConfig;
import ru.yandex.practicum.paymentservice.dto.OrderDto;
import ru.yandex.practicum.paymentservice.entity.Order;

@Mapper(config = GlobalMapperConfig.class)
public interface OrderMapper {

    @Mapping(target = "items", source = "items")
    OrderDto toDto(Order order);

    List<OrderDto> toDtoList(List<Order> orders);
}
