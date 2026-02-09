package com.amalitech.smartshop.repositories;

import com.amalitech.smartshop.entities.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartRepository implements com.amalitech.smartshop.interfaces.CartRepository {
    private final Connection connection;

    private Cart mapRow(ResultSet rs) throws SQLException {
        return Cart.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    @Override
    public Cart save(Cart cart) {
        try {
            if (cart.getId() == null) {
                return insert(cart);
            } else {
                return update(cart);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving cart", e);
        }
    }

    private Cart insert(Cart cart) throws SQLException {
        String sql = "INSERT INTO cart (user_id, created_at, updated_at) VALUES (?, NOW(), NOW())";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, cart.getUserId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    cart.setId(keys.getLong(1));
                }
            }
        }
        return cart;
    }

    private Cart update(Cart cart) throws SQLException {
        String sql = "UPDATE cart SET updated_at = NOW() WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cart.getId());
            ps.executeUpdate();
        }
        return cart;
    }

    @Override
    public Optional<Cart> findById(Long id) {
        String sql = "SELECT * FROM cart WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding cart by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        String sql = "SELECT * FROM cart WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding cart by user id", e);
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM cart WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting cart", e);
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting cart by user id", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM cart WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking cart existence", e);
        }
        return false;
    }
}
