package com.amalitech.smartshop.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a product review in the SmartShop e-commerce platform.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private Long id;
    private Long productId;
    private Long userId;
    private Integer rating; // 1-5
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
