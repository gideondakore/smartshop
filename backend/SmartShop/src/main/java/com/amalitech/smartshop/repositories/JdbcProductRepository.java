package com.amalitech.smartshop.repositories;

import com.amalitech.smartshop.entities.Product;
import com.amalitech.smartshop.interfaces.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-based implementation of the ProductRepository interface.
 */
@Repository
public class JdbcProductRepository implements ProductRepository {
    private final Connection connection;

    public JdbcProductRepository(Connection connection) {
        this.connection = connection;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return Product.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .imageUrl(rs.getString("image_url"))
                .sku(rs.getString("sku"))
                .price(rs.getDouble("price"))
                .vendorId(rs.getObject("vendor_id") != null ? rs.getLong("vendor_id") : null)
                .available(rs.getBoolean("is_available"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .categoryId(rs.getLong("category_id"))
                .build();
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        String sql = "SELECT COUNT(*) FROM products WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking product existence", e);
        }
        return false;
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id WHERE p.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Product save(Product product) {
        try {
            if (product.getId() == null) {
                return insert(product);
            } else {
                return update(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving product", e);
        }
    }

    private Product insert(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, description, image_url, category_id, sku, price, vendor_id, is_available, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getImageUrl());
            ps.setLong(4, product.getCategoryId());
            ps.setString(5, product.getSku());
            ps.setDouble(6, product.getPrice());
            if (product.getVendorId() != null) {
                ps.setLong(7, product.getVendorId());
            } else {
                ps.setNull(7, java.sql.Types.BIGINT);
            }
            ps.setBoolean(8, product.isAvailable());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    product.setId(keys.getLong(1));
                }
            }
        }
        return product;
    }

    private Product update(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, description = ?, image_url = ?, category_id = ?, sku = ?, price = ?, vendor_id = ?, is_available = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getImageUrl());
            ps.setLong(4, product.getCategoryId());
            ps.setString(5, product.getSku());
            ps.setDouble(6, product.getPrice());
            if (product.getVendorId() != null) {
                ps.setLong(7, product.getVendorId());
            } else {
                ps.setNull(7, java.sql.Types.BIGINT);
            }
            ps.setBoolean(8, product.isAvailable());
            ps.setLong(9, product.getId());
            ps.executeUpdate();
        }
        return product;
    }

    @Override
    public void delete(Product product) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product", e);
        }
    }

    @Override
    public Page<Product> findByCategoryId(Long categoryId, Pageable pageable) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id WHERE p.category_id = ? LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, categoryId);
            ps.setInt(2, pageable.getPageSize());
            ps.setInt(3, (int) pageable.getOffset());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by category", e);
        }
        
        long total = countByCategory(categoryId);
        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id WHERE p.category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by category", e);
        }
        return products;
    }

    @Override
    public Page<Product> findByVendorId(Long vendorId, Pageable pageable) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id WHERE p.vendor_id = ? LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, vendorId);
            ps.setInt(2, pageable.getPageSize());
            ps.setInt(3, (int) pageable.getOffset());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by vendor", e);
        }
        
        long total = countByVendor(vendorId);
        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pageable.getPageSize());
            ps.setInt(2, (int) pageable.getOffset());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all products", e);
        }
        
        long total = countProducts();
        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Page<Product> findAllWithInventory(Pageable pageable) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id WHERE EXISTS (SELECT 1 FROM inventory i WHERE i.product_id = p.id) LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pageable.getPageSize());
            ps.setInt(2, (int) pageable.getOffset());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products with inventory", e);
        }
        
        long total = countProductsWithInventory();
        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public Page<Product> findByCategoryIdWithInventory(Long categoryId, Pageable pageable) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id WHERE p.category_id = ? AND EXISTS (SELECT 1 FROM inventory i WHERE i.product_id = p.id) LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, categoryId);
            ps.setInt(2, pageable.getPageSize());
            ps.setInt(3, (int) pageable.getOffset());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by category with inventory", e);
        }
        
        long total = countByCategoryWithInventory(categoryId);
        return new PageImpl<>(products, pageable, total);
    }

    @Override
    public List<Product> findAllWithInventory() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p JOIN categories c ON p.category_id = c.id WHERE EXISTS (SELECT 1 FROM inventory i WHERE i.product_id = p.id)";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all products with inventory", e);
        }
        return products;
    }

    private long countProducts() {
        String countSql = "SELECT COUNT(*) FROM products";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting products", e);
        }
        return 0;
    }

    private long countByCategory(Long categoryId) {
        String countSql = "SELECT COUNT(*) FROM products WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(countSql)) {
            ps.setLong(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting products by category", e);
        }
        return 0;
    }

    private long countProductsWithInventory() {
        String countSql = "SELECT COUNT(*) FROM products p WHERE EXISTS (SELECT 1 FROM inventory i WHERE i.product_id = p.id)";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting products with inventory", e);
        }
        return 0;
    }

    private long countByCategoryWithInventory(Long categoryId) {
        String countSql = "SELECT COUNT(*) FROM products p WHERE p.category_id = ? AND EXISTS (SELECT 1 FROM inventory i WHERE i.product_id = p.id)";
        try (PreparedStatement ps = connection.prepareStatement(countSql)) {
            ps.setLong(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting products by category with inventory", e);
        }
        return 0;
    }

    private long countByVendor(Long vendorId) {
        String countSql = "SELECT COUNT(*) FROM products WHERE vendor_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(countSql)) {
            ps.setLong(1, vendorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting products by vendor", e);
        }
        return 0;
    }
}
