package com.amalitech.smartshop.mappers;

import com.amalitech.smartshop.dtos.responses.InventoryResponseDTO;
import com.amalitech.smartshop.entities.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Inventory entity conversions.
 */
@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "productId", target = "productId")
    @Mapping(target = "productName", ignore = true)
    InventoryResponseDTO toResponseDTO(Inventory inventory);
}
