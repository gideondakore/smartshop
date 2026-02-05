package com.amalitech.smartshop.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a product in the SmartShop e-commerce catalog.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;
    private String name;
    private Long categoryId;
    private String sku;
    private Double price;
    @Builder.Default
    private boolean available = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
