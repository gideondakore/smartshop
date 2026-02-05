package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.dtos.requests.AddInventoryDTO;
import com.amalitech.smartshop.dtos.requests.UpdateInventoryDTO;
import com.amalitech.smartshop.dtos.responses.InventoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for inventory-related business operations.
 */
public interface InventoryService {

    /**
     * Add a new inventory record.
     *
     * @param addInventoryDTO the inventory data
     * @return the created inventory response
     */
    InventoryResponseDTO addInventory(AddInventoryDTO addInventoryDTO);

    /**
     * Get all inventory records with pagination.
     *
     * @param pageable pagination information
     * @return a page of inventory responses
     */
    Page<InventoryResponseDTO> getAllInventories(Pageable pageable);

    /**
     * Get an inventory record by its ID.
     *
     * @param id the inventory ID
     * @return the inventory response
     */
    InventoryResponseDTO getInventoryById(Long id);

    /**
     * Get inventory by product ID.
     *
     * @param productId the product ID
     * @return the inventory response
     */
    InventoryResponseDTO getInventoryByProductId(Long productId);

    /**
     * Update an inventory record.
     *
     * @param id the inventory ID
     * @param updateInventoryDTO the update data
     * @return the updated inventory response
     */
    InventoryResponseDTO updateInventory(Long id, UpdateInventoryDTO updateInventoryDTO);

    /**
     * Adjust inventory quantity by a delta value.
     *
     * @param id the inventory ID
     * @param quantityChange the quantity change (positive or negative)
     * @return the updated inventory response
     */
    InventoryResponseDTO adjustInventoryQuantity(Long id, Integer quantityChange);

    /**
     * Delete an inventory record.
     *
     * @param id the inventory ID to delete
     */
    void deleteInventory(Long id);
}
