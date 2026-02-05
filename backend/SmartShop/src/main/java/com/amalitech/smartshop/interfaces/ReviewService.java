package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.dtos.requests.AddReviewDTO;
import com.amalitech.smartshop.dtos.requests.UpdateReviewDTO;
import com.amalitech.smartshop.dtos.responses.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponseDTO addReview(AddReviewDTO request, Long userId);
    ReviewResponseDTO updateReview(Long id, UpdateReviewDTO request, Long userId);
    void deleteReview(Long id, Long userId);
    ReviewResponseDTO getReviewById(Long id);
    Page<ReviewResponseDTO> getAllReviews(Pageable pageable);
    Page<ReviewResponseDTO> getReviewsByProductId(Long productId, Pageable pageable);
    Page<ReviewResponseDTO> getReviewsByUserId(Long userId, Pageable pageable);
}
