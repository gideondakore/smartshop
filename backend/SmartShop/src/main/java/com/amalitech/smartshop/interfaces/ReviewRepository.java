package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewRepository {
    Review save(Review review);
    Optional<Review> findById(Long id);
    Page<Review> findAll(Pageable pageable);
    Page<Review> findByProductId(Long productId, Pageable pageable);
    Page<Review> findByUserId(Long userId, Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);
}
