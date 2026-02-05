package com.amalitech.smartshop.dtos.requests;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateProductDTO {
    private String name;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private String sku;
    @Positive(message = "Price must be positive")
    private Double price;
    private Boolean isAvailable;
}
