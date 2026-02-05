package com.amalitech.smartshop.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a shopping cart in the SmartShop e-commerce platform.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
