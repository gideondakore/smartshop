package com.amalitech.smartshop.entities;

import com.amalitech.smartshop.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a customer order in the SmartShop e-commerce system.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private Long userId;
    private Double totalAmount;
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
