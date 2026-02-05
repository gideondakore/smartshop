package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity operations.
 */
public interface OrderRepository {

    /**
     * Find all orders for a user with pagination.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return a page of orders for the user
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * Find all orders for a user.
     *
     * @param userId the user ID
     * @return list of orders for the user
     */
    List<Order> findByUserId(Long userId);

    /**
     * Find all orders with pagination.
     *
     * @param pageable pagination information
     * @return a page of orders
     */
    Page<Order> findAll(Pageable pageable);

    /**
     * Find an order by its ID.
     *
     * @param id the order ID
     * @return an Optional containing the order if found
     */
    Optional<Order> findById(Long id);

    /**
     * Save or update an order.
     *
     * @param order the order to save
     * @return the saved order
     */
    Order save(Order order);

    /**
     * Delete an order.
     *
     * @param order the order to delete
     */
    void delete(Order order);
}
