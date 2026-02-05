package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.dtos.requests.AddOrderDTO;
import com.amalitech.smartshop.dtos.requests.UpdateOrderDTO;
import com.amalitech.smartshop.dtos.responses.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for order-related business operations.
 */
public interface OrderService {

    /**
     * Create a new order.
     *
     * @param addOrderDTO the order data
     * @return the created order response
     */
    OrderResponseDTO createOrder(AddOrderDTO addOrderDTO);

    /**
     * Get all orders with pagination.
     *
     * @param pageable pagination information
     * @return a page of order responses
     */
    Page<OrderResponseDTO> getAllOrders(Pageable pageable);

    /**
     * Get orders for a specific user with pagination.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return a page of order responses
     */
    Page<OrderResponseDTO> getOrdersByUserId(Long userId, Pageable pageable);

    /**
     * Get an order by its ID.
     *
     * @param id the order ID
     * @return the order response
     */
    OrderResponseDTO getOrderById(Long id);

    /**
     * Update an order's status.
     *
     * @param id the order ID
     * @param updateOrderDTO the update data
     * @return the updated order response
     */
    OrderResponseDTO updateOrderStatus(Long id, UpdateOrderDTO updateOrderDTO);

    /**
     * Delete an order and its items.
     *
     * @param id the order ID to delete
     */
    void deleteOrder(Long id);
}
