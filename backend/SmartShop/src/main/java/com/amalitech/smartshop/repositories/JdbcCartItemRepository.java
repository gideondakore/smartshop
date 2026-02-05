package com.amalitech.smartshop.repositories;

import com.amalitech.smartshop.entities.CartItem;
import com.amalitech.smartshop.interfaces.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcCartItemRepository implements CartItemRepository {
    private final Connection connection;

    private CartItem mapRow(ResultSet rs) throws SQLException {
        return CartItem.builder()
                .id(rs.getLong("id"))
                .cartId(rs.getLong("cart_id"))
                .productId(rs.getLong("product_id"))
                .quantity(rs.getInt("quantity"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    @Override
    public CartItem save(CartItem cartItem) {
        try {
            if (cartItem.getId() == null) {
                return insert(cartItem);
            } else {
                return update(cartItem);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving cart item", e);
        }
    }

    private CartItem insert(CartItem cartItem) throws SQLException {
        String sql = "INSERT INTO cart_items (cart_id, product_id, quantity, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, cartItem.getCartId());
            ps.setLong(2, cartItem.getProductId());
            ps.setInt(3, cartItem.getQuantity());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    cartItem.setId(keys.getLong(1));
                }
            }
        }
        return cartItem;
    }

    private CartItem update(CartItem cartItem) throws SQLException {
        String sql = "UPDATE cart_items SET quantity = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cartItem.getQuantity());
            ps.setLong(2, cartItem.getId());
            ps.executeUpdate();
        }
        return cartItem;
    }

    @Override
    public Optional<CartItem> findById(Long id) {
        String sql = "SELECT * FROM cart_items WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding cart item by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<CartItem> findByCartId(Long cartId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT * FROM cart_items WHERE cart_id = ? ORDER BY created_at ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding cart items by cart id", e);
        }
        return items;
    }

    @Override
    public Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId) {
        String sql = "SELECT * FROM cart_items WHERE cart_id = ? AND product_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cartId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding cart item by cart and product id", e);
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM cart_items WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting cart item", e);
        }
    }

    @Override
    public void deleteByCartId(Long cartId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cartId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting cart items by cart id", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM cart_items WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking cart item existence", e);
        }
        return false;
    }
}
