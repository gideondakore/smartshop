package com.amalitech.smartshop.services;

import com.amalitech.smartshop.cache.CacheManager;
import com.amalitech.smartshop.dtos.requests.AddOrderDTO;
import com.amalitech.smartshop.dtos.requests.OrderItemDTO;
import com.amalitech.smartshop.dtos.requests.UpdateOrderDTO;
import com.amalitech.smartshop.dtos.responses.OrderItemResponseDTO;
import com.amalitech.smartshop.dtos.responses.OrderResponseDTO;
import com.amalitech.smartshop.entities.Inventory;
import com.amalitech.smartshop.entities.Order;
import com.amalitech.smartshop.entities.OrderItem;
import com.amalitech.smartshop.entities.Product;
import com.amalitech.smartshop.enums.OrderStatus;
import com.amalitech.smartshop.exceptions.ConstraintViolationException;
import com.amalitech.smartshop.exceptions.ResourceNotFoundException;
import com.amalitech.smartshop.interfaces.*;
import com.amalitech.smartshop.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the OrderService interface.
 * Handles all order-related business logic including order creation,
 * status updates, and inventory management.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderMapper orderMapper;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(AddOrderDTO addOrderDTO) {
        log.info("Creating order for user: {}", addOrderDTO.getUserId());
        
        userRepository.findById(addOrderDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + addOrderDTO.getUserId()));

        List<OrderItem> orderItems = new ArrayList<>();
        List<Inventory> inventoriesToUpdate = new ArrayList<>();
        double totalAmount = 0.0;

        for (OrderItemDTO itemDTO : addOrderDTO.getItems()) {
            Product product = validateAndGetProduct(itemDTO.getProductId());
            Inventory inventory = validateAndReserveInventory(product, itemDTO.getQuantity());
            
            inventoriesToUpdate.add(inventory);
            invalidateProductCache(product.getId());

            double itemTotal = product.getPrice() * itemDTO.getQuantity();
            totalAmount += itemTotal;

            orderItems.add(OrderItem.builder()
                    .productId(product.getId())
                    .quantity(itemDTO.getQuantity())
                    .totalPrice(itemTotal)
                    .build());
        }

        inventoryRepository.saveAll(inventoriesToUpdate);

        Order order = Order.builder()
                .userId(addOrderDTO.getUserId())
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();
        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(savedOrder.getId());
        }
        List<OrderItem> savedItems = orderItemRepository.saveAll(orderItems);

        log.info("Order created successfully with id: {}", savedOrder.getId());
        return buildOrderResponse(savedOrder, savedItems);
    }

    @Override
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(order ->
                cacheManager.get("order:" + order.getId(), () -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return buildOrderResponse(order, items);
                })
        );
    }

    @Override
    public Page<OrderResponseDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return orderRepository.findByUserId(userId, pageable).map(order ->
                cacheManager.get("order:" + order.getId(), () -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return buildOrderResponse(order, items);
                })
        );
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return buildOrderResponse(order, items);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, UpdateOrderDTO updateOrderDTO) {
        log.info("Updating order status: {} to {}", id, updateOrderDTO.getStatus());
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        if (updateOrderDTO.getStatus() != null) {
            order.setStatus(updateOrderDTO.getStatus());
        }

        Order updatedOrder = orderRepository.save(order);
        cacheManager.invalidate("order:" + id);

        List<OrderItem> items = orderItemRepository.findByOrderId(updatedOrder.getId());
        
        log.info("Order status updated successfully: {}", id);
        return buildOrderResponse(updatedOrder, items);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        log.info("Deleting order: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        try {
            List<OrderItem> items = orderItemRepository.findByOrderId(id);
            orderItemRepository.deleteAll(items);
            orderRepository.delete(order);

            cacheManager.invalidate("order:" + id);
            log.info("Order deleted successfully: {}", id);
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("foreign key constraint")) {
                throw new ConstraintViolationException(
                        "Cannot delete order. It has related dependencies that must be removed first.");
            }
            throw ex;
        }
    }

    private Product validateAndGetProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!product.isAvailable()) {
            throw new IllegalArgumentException("Product '" + product.getName() + "' is not available");
        }
        return product;
    }

    private Inventory validateAndReserveInventory(Product product, int requestedQuantity) {
        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product '" + product.getName() + "' is out of stock"));

        if (inventory.getQuantity() < requestedQuantity) {
            throw new IllegalArgumentException("Product '" + product.getName() + "' is out of stock");
        }

        inventory.setQuantity(inventory.getQuantity() - requestedQuantity);
        return inventory;
    }

    private void invalidateProductCache(Long productId) {
        cacheManager.invalidate("product:" + productId);
        cacheManager.invalidate("inventory:product:" + productId);
        cacheManager.invalidate("inventory:quantity:" + productId);
    }

    private OrderResponseDTO buildOrderResponse(Order order, List<OrderItem> items) {
        OrderResponseDTO response = orderMapper.toResponseDTO(order);

        userRepository.findById(order.getUserId())
                .ifPresent(user -> response.setUserName(user.getFullName()));

        List<OrderItemResponseDTO> itemResponses = items.stream()
                .map(item -> {
                    OrderItemResponseDTO itemResponse = orderMapper.toOrderItemResponseDTO(item);
                    productRepository.findById(item.getProductId())
                            .ifPresent(product -> itemResponse.setProductName(product.getName()));
                    return itemResponse;
                })
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }
}
