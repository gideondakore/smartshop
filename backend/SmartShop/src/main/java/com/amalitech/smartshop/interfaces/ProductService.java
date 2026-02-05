package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.dtos.requests.AddProductDTO;
import com.amalitech.smartshop.dtos.requests.UpdateProductDTO;
import com.amalitech.smartshop.dtos.responses.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for product-related business operations.
 */
public interface ProductService {

    /**
     * Add a new product.
     *
     * @param addProductDTO the product data
     * @return the created product response
     */
    ProductResponseDTO addProduct(AddProductDTO addProductDTO);

    /**
     * Get all products with pagination.
     *
     * @param pageable pagination information
     * @param isAdmin whether the request is from an admin user
     * @return a page of product responses
     */
    Page<ProductResponseDTO> getAllProducts(Pageable pageable, boolean isAdmin);

    /**
     * Get products by category with pagination.
     *
     * @param categoryId the category ID
     * @param pageable pagination information
     * @param isAdmin whether the request is from an admin user
     * @return a page of product responses
     */
    Page<ProductResponseDTO> getProductsByCategory(Long categoryId, Pageable pageable, boolean isAdmin);

    /**
     * Get a product by its ID.
     *
     * @param id the product ID
     * @return the product response
     */
    ProductResponseDTO getProductById(Long id);

    /**
     * Update a product.
     *
     * @param id the product ID
     * @param updateProductDTO the update data
     * @return the updated product response
     */
    ProductResponseDTO updateProduct(Long id, UpdateProductDTO updateProductDTO);

    /**
     * Get all products as a list.
     *
     * @return list of all product responses
     */
    List<ProductResponseDTO> getAllProductsList();

    /**
     * Delete a product.
     *
     * @param id the product ID to delete
     */
    void deleteProduct(Long id);
}
