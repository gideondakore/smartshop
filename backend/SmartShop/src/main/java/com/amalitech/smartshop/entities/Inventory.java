package com.amalitech.smartshop.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents inventory information for a product in the SmartShop e-commerce system.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    private Long id;
    private Long productId;
    private Integer quantity;
    private String location;
}
