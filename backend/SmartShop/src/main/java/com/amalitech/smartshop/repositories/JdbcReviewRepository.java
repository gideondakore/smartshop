package com.amalitech.smartshop.repositories;

import com.amalitech.smartshop.entities.Review;
import com.amalitech.smartshop.interfaces.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcReviewRepository implements ReviewRepository {
    private final Connection connection;

    private Review mapRow(ResultSet rs) throws SQLException {
        return Review.builder()
                .id(rs.getLong("id"))
                .productId(rs.getLong("product_id"))
                .userId(rs.getLong("user_id"))
                .rating(rs.getInt("rating"))
                .comment(rs.getString("comment"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    @Override
    public Review save(Review review) {
        try {
            if (review.getId() == null) {
                return insert(review);
            } else {
                return update(review);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving review", e);
        }
    }

    private Review insert(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (product_id, user_id, rating, comment, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, review.getProductId());
            ps.setLong(2, review.getUserId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    review.setId(keys.getLong(1));
                }
            }
        }
        return review;
    }

    private Review update(Review review) throws SQLException {
        String sql = "UPDATE reviews SET rating = ?, comment = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setLong(3, review.getId());
            ps.executeUpdate();
        }
        return review;
    }

    @Override
    public Optional<Review> findById(Long id) {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding review by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pageable.getPageSize());
            ps.setInt(2, (int) pageable.getOffset());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all reviews", e);
        }
        
        long total = count();
        return new PageImpl<>(reviews, pageable, total);
    }

    @Override
    public Page<Review> findByProductId(Long productId, Pageable pageable) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE product_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setInt(2, pageable.getPageSize());
            ps.setInt(3, (int) pageable.getOffset());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reviews by product", e);
        }
        
        long total = countByProductId(productId);
        return new PageImpl<>(reviews, pageable, total);
    }

    @Override
    public Page<Review> findByUserId(Long userId, Pageable pageable) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, pageable.getPageSize());
            ps.setInt(3, (int) pageable.getOffset());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reviews by user", e);
        }
        
        long total = countByUserId(userId);
        return new PageImpl<>(reviews, pageable, total);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting review", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking review existence", e);
        }
        return false;
    }

    private long count() {
        String sql = "SELECT COUNT(*) FROM reviews";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting reviews", e);
        }
        return 0;
    }

    private long countByProductId(Long productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE product_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting reviews by product", e);
        }
        return 0;
    }

    private long countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting reviews by user", e);
        }
        return 0;
    }
}
