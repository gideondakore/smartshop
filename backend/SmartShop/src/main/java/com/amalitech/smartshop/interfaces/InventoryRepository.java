package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.entities.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Inventory entity operations.
 */
public interface InventoryRepository {

    /**
     * Find inventory by product ID.
     *
     * @param productId the product ID
     * @return an Optional containing the inventory if found
     */
    Optional<Inventory> findByProductId(Long productId);

    /**
     * Check if inventory exists for a product.
     *
     * @param productId the product ID
     * @return true if inventory exists for the product
     */
    boolean existsByProductId(Long productId);

    /**
     * Find inventory by its ID.
     *
     * @param id the inventory ID
     * @return an Optional containing the inventory if found
     */
    Optional<Inventory> findById(Long id);

    /**
     * Find all inventory records with pagination.
     *
     * @param pageable pagination information
     * @return a page of inventory records
     */
    Page<Inventory> findAll(Pageable pageable);

    /**
     * Save or update an inventory record.
     *
     * @param inventory the inventory to save
     * @return the saved inventory
     */
    Inventory save(Inventory inventory);

    /**
     * Save multiple inventory records.
     *
     * @param inventories the inventory records to save
     * @return the saved inventory records
     */
    List<Inventory> saveAll(List<Inventory> inventories);

    /**
     * Delete an inventory record.
     *
     * @param inventory the inventory to delete
     */
    void delete(Inventory inventory);
}
