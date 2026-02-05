package com.amalitech.smartshop.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a user session in the SmartShop e-commerce platform.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private Long id;
    private String token;
    private Long userId;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
