package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.entities.OrderItem;

import java.util.List;

/**
 * Repository interface for OrderItem entity operations.
 */
public interface OrderItemRepository {

    /**
     * Find all order items for an order.
     *
     * @param orderId the order ID
     * @return list of order items for the order
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Save multiple order items.
     *
     * @param items the order items to save
     * @return the saved order items
     */
    List<OrderItem> saveAll(List<OrderItem> items);

    /**
     * Save a single order item.
     *
     * @param item the order item to save
     * @return the saved order item
     */
    OrderItem save(OrderItem item);

    /**
     * Delete multiple order items.
     *
     * @param items the order items to delete
     */
    void deleteAll(List<OrderItem> items);

    /**
     * Delete a single order item.
     *
     * @param item the order item to delete
     */
    void delete(OrderItem item);
}
