package com.amalitech.smartshop.repositories;

import com.amalitech.smartshop.entities.Order;
import com.amalitech.smartshop.enums.OrderStatus;
import com.amalitech.smartshop.interfaces.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-based implementation of the OrderRepository interface.
 */
@Repository
public class JdbcOrderRepository implements OrderRepository {
    private final Connection connection;

    public JdbcOrderRepository(Connection connection) {
        this.connection = connection;
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        return Order.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .totalAmount(rs.getDouble("total_amount"))
                .status(OrderStatus.valueOf(rs.getString("status")))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    @Override
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        List<Order> orders = new ArrayList<>();
        boolean paged = pageable != null && pageable.isPaged();
        String sql = paged
                ? "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?"
                : "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            if (paged) {
                ps.setInt(2, pageable.getPageSize());
                ps.setInt(3, (int) pageable.getOffset());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding orders by user id", e);
        }
        
        long total = countByUserId(userId);
        return new PageImpl<>(orders, pageable == null ? Pageable.unpaged() : pageable, total);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding orders by user id", e);
        }
        return orders;
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        List<Order> orders = new ArrayList<>();
        boolean paged = pageable != null && pageable.isPaged();
        String sql = paged
                ? "SELECT o.*, u.email as user_email FROM orders o JOIN users u ON o.user_id = u.id ORDER BY o.created_at DESC LIMIT ? OFFSET ?"
                : "SELECT o.*, u.email as user_email FROM orders o JOIN users u ON o.user_id = u.id ORDER BY o.created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (paged) {
                ps.setInt(1, pageable.getPageSize());
                ps.setInt(2, (int) pageable.getOffset());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all orders", e);
        }
        
        long total = countOrders();
        return new PageImpl<>(orders, pageable == null ? Pageable.unpaged() : pageable, total);
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = "SELECT o.*, u.email as user_email FROM orders o JOIN users u ON o.user_id = u.id WHERE o.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Order save(Order order) {
        try {
            if (order.getId() == null) {
                return insert(order);
            } else {
                return update(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order", e);
        }
    }

    private Order insert(Order order) throws SQLException {
        String sql = "INSERT INTO orders (user_id, total_amount, status, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, order.getUserId());
            ps.setDouble(2, order.getTotalAmount());
            ps.setString(3, order.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setId(keys.getLong(1));
                }
            }
        }
        return order;
    }

    private Order update(Order order) throws SQLException {
        String sql = "UPDATE orders SET user_id = ?, total_amount = ?, status = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, order.getUserId());
            ps.setDouble(2, order.getTotalAmount());
            ps.setString(3, order.getStatus().name());
            ps.setLong(4, order.getId());
            ps.executeUpdate();
        }
        return order;
    }

    @Override
    public void delete(Order order) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, order.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order", e);
        }
    }

    private long countOrders() {
        String countSql = "SELECT COUNT(*) FROM orders";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting orders", e);
        }
        return 0;
    }

    private long countByUserId(Long userId) {
        String countSql = "SELECT COUNT(*) FROM orders WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(countSql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting orders by user id", e);
        }
        return 0;
    }
}
