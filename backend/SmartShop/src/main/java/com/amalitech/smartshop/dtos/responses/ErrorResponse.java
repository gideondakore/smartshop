package com.amalitech.smartshop.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard error response structure for all error cases in the SmartShop API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response structure for all error cases")
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2026-01-19T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "404")
    private Integer status;

    @Schema(description = "HTTP status reason phrase", example = "Not Found")
    private String error;

    @Schema(description = "Error message describing what went wrong", example = "Product not found with id: 123")
    private String message;

    @Schema(description = "Request path that caused the error", example = "/api/products/123")
    private String path;
}
