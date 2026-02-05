package com.amalitech.smartshop.repositories;

import com.amalitech.smartshop.entities.Inventory;
import com.amalitech.smartshop.interfaces.InventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-based implementation of the InventoryRepository interface.
 */
@Repository
public class JdbcInventoryRepository implements InventoryRepository {
    private final Connection connection;

    public JdbcInventoryRepository(Connection connection) {
        this.connection = connection;
    }

    private Inventory mapRow(ResultSet rs) throws SQLException {
        return Inventory.builder()
                .id(rs.getLong("id"))
                .productId(rs.getLong("product_id"))
                .quantity(rs.getInt("quantity"))
                .location(rs.getString("location"))
                .build();
    }

    @Override
    public Optional<Inventory> findByProductId(Long productId) {
        String sql = "SELECT * FROM inventory WHERE product_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory by product id", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByProductId(Long productId) {
        String sql = "SELECT COUNT(*) FROM inventory WHERE product_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking inventory existence", e);
        }
        return false;
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        String sql = "SELECT * FROM inventory WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Page<Inventory> findAll(Pageable pageable) {
        List<Inventory> inventories = new ArrayList<>();
        String sql;

        if (pageable.isPaged()) {
            sql = "SELECT * FROM inventory LIMIT ? OFFSET ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, pageable.getPageSize());
                ps.setInt(2, (int) pageable.getOffset());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        inventories.add(mapRow(rs));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error finding all inventories", e);
            }
        } else {
            sql = "SELECT * FROM inventory";
            try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    inventories.add(mapRow(rs));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error finding all inventories", e);
            }
        }

        long total = countInventories();
        return new PageImpl<>(inventories, pageable, total);
    }

    @Override
    public Inventory save(Inventory inventory) {
        try {
            if (inventory.getId() == null) {
                return insert(inventory);
            } else {
                return update(inventory);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving inventory", e);
        }
    }

    private Inventory insert(Inventory inventory) throws SQLException {
        String sql = "INSERT INTO inventory (product_id, quantity, location) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, inventory.getProductId());
            ps.setInt(2, inventory.getQuantity());
            ps.setString(3, inventory.getLocation());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    inventory.setId(keys.getLong(1));
                }
            }
        }
        return inventory;
    }

    private Inventory update(Inventory inventory) throws SQLException {
        String sql = "UPDATE inventory SET product_id = ?, quantity = ?, location = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, inventory.getProductId());
            ps.setInt(2, inventory.getQuantity());
            ps.setString(3, inventory.getLocation());
            ps.setLong(4, inventory.getId());
            ps.executeUpdate();
        }
        return inventory;
    }

    @Override
    public List<Inventory> saveAll(List<Inventory> inventories) {
        for (Inventory inventory : inventories) {
            save(inventory);
        }
        return inventories;
    }

    @Override
    public void delete(Inventory inventory) {
        String sql = "DELETE FROM inventory WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, inventory.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting inventory", e);
        }
    }

    private long countInventories() {
        String countSql = "SELECT COUNT(*) FROM inventory";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting inventories", e);
        }
        return 0;
    }
}
