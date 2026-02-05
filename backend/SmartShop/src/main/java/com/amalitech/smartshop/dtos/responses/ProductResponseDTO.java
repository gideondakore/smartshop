package com.amalitech.smartshop.dtos.responses;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String sku;
    private Double price;
    private Integer quantity;
    private Long categoryId;
    private String categoryName;
    private Long vendorId;
}
