package com.amalitech.smartshop.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents an item in a shopping cart.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Long id;
    private Long cartId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
