package com.amalitech.smartshop.mappers;

import com.amalitech.smartshop.dtos.responses.OrderItemResponseDTO;
import com.amalitech.smartshop.dtos.responses.OrderResponseDTO;
import com.amalitech.smartshop.entities.Order;
import com.amalitech.smartshop.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Order entity conversions.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "userId", target = "userId")
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "items", ignore = true)
    OrderResponseDTO toResponseDTO(Order order);

    @Mapping(source = "productId", target = "productId")
    @Mapping(target = "productName", ignore = true)
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);
}
