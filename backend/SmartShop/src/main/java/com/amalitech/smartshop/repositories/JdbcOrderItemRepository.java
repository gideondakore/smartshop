package com.amalitech.smartshop.repositories;

import com.amalitech.smartshop.entities.OrderItem;
import com.amalitech.smartshop.interfaces.OrderItemRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-based implementation of the OrderItemRepository interface.
 */
@Repository
public class JdbcOrderItemRepository implements OrderItemRepository {
    private final Connection connection;

    public JdbcOrderItemRepository(Connection connection) {
        this.connection = connection;
    }

    private OrderItem mapRow(ResultSet rs) throws SQLException {
        return OrderItem.builder()
                .id(rs.getLong("id"))
                .orderId(rs.getLong("order_id"))
                .productId(rs.getLong("product_id"))
                .quantity(rs.getInt("quantity"))
                .totalPrice(rs.getDouble("total_price"))
                .build();
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name as product_name FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order items by order id", e);
        }
        return items;
    }

    @Override
    public List<OrderItem> saveAll(List<OrderItem> items) {
        for (OrderItem item : items) {
            save(item);
        }
        return items;
    }

    @Override
    public OrderItem save(OrderItem item) {
        try {
            if (item.getId() == null) {
                return insert(item);
            } else {
                return update(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order item", e);
        }
    }

    private OrderItem insert(OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, total_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, item.getOrderId());
            ps.setLong(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getTotalPrice());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getLong(1));
                }
            }
        }
        return item;
    }

    private OrderItem update(OrderItem item) throws SQLException {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ?, total_price = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, item.getOrderId());
            ps.setLong(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getTotalPrice());
            ps.setLong(5, item.getId());
            ps.executeUpdate();
        }
        return item;
    }

    @Override
    public void deleteAll(List<OrderItem> items) {
        for (OrderItem item : items) {
            delete(item);
        }
    }

    @Override
    public void delete(OrderItem item) {
        String sql = "DELETE FROM order_items WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order item", e);
        }
    }
}
