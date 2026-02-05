package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity operations.
 */
public interface ProductRepository {

    /**
     * Check if a product exists with the given name (case-insensitive).
     *
     * @param name the product name to check
     * @return true if a product with the name exists
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find a product by its ID.
     *
     * @param id the product ID
     * @return an Optional containing the product if found
     */
    Optional<Product> findById(Long id);

    /**
     * Save or update a product.
     *
     * @param product the product to save
     * @return the saved product
     */
    Product save(Product product);

    /**
     * Delete a product.
     *
     * @param product the product to delete
     */
    void delete(Product product);

    /**
     * Find all products with pagination.
     *
     * @param pageable pagination information
     * @return a page of products
     */
    Page<Product> findAll(Pageable pageable);

    /**
     * Find all products by category ID with pagination.
     *
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return a page of products in the category
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Find all products by category ID.
     *
     * @param categoryId the category ID
     * @return list of products in the category
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Find all products by vendor ID with pagination.
     *
     * @param vendorId the vendor ID
     * @param pageable pagination information
     * @return a page of products owned by the vendor
     */
    Page<Product> findByVendorId(Long vendorId, Pageable pageable);

    /**
     * Find all products that have inventory with pagination.
     *
     * @param pageable pagination information
     * @return a page of products with inventory
     */
    Page<Product> findAllWithInventory(Pageable pageable);

    /**
     * Find products by category that have inventory with pagination.
     *
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return a page of products with inventory in the category
     */
    Page<Product> findByCategoryIdWithInventory(Long categoryId, Pageable pageable);

    /**
     * Find all products that have inventory.
     *
     * @return list of all products with inventory
     */
    List<Product> findAllWithInventory();
}
