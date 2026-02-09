package com.amalitech.smartshop.controllers;

import com.amalitech.smartshop.config.RequiresRole;
import com.amalitech.smartshop.dtos.requests.AddReviewDTO;
import com.amalitech.smartshop.dtos.requests.UpdateReviewDTO;
import com.amalitech.smartshop.dtos.responses.ApiResponse;
import com.amalitech.smartshop.dtos.responses.PagedResponse;
import com.amalitech.smartshop.dtos.responses.ReviewResponseDTO;
import com.amalitech.smartshop.enums.UserRole;
import com.amalitech.smartshop.interfaces.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for review management operations.
 * Handles CRUD operations for product reviews.
 */
@Tag(name = "Review Management", description = "APIs for managing product reviews")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Add a new review")
    @RequiresRole(UserRole.CUSTOMER)
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> addReview(
            @Valid @RequestBody AddReviewDTO request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authUserId");
        ReviewResponseDTO review = reviewService.addReview(request, userId);
        ApiResponse<ReviewResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Review added successfully", review);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update a review")
    @RequiresRole(UserRole.CUSTOMER)
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewDTO request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authUserId");
        ReviewResponseDTO updatedReview = reviewService.updateReview(id, request, userId);
        ApiResponse<ReviewResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Review updated successfully", updatedReview);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Delete a review")
    @RequiresRole(UserRole.CUSTOMER)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authUserId");
        reviewService.deleteReview(id, userId);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Review deleted successfully", null);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get review by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> getReviewById(@PathVariable Long id) {
        ReviewResponseDTO review = reviewService.getReviewById(id);
        ApiResponse<ReviewResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Review fetched successfully", review);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get all reviews")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponseDTO>>> getAllReviews(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<ReviewResponseDTO> reviews = reviewService.getAllReviews(pageable);
        PagedResponse<ReviewResponseDTO> pagedResponse = new PagedResponse<>(
                reviews.getContent(),
                reviews.getNumber(),
                (int) reviews.getTotalElements(),
                reviews.getTotalPages(),
                reviews.isLast()
        );
        ApiResponse<PagedResponse<ReviewResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Reviews fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get reviews by product ID")
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponseDTO>>> getReviewsByProductId(
            @PathVariable Long productId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<ReviewResponseDTO> reviews = reviewService.getReviewsByProductId(productId, pageable);
        PagedResponse<ReviewResponseDTO> pagedResponse = new PagedResponse<>(
                reviews.getContent(),
                reviews.getNumber(),
                (int) reviews.getTotalElements(),
                reviews.getTotalPages(),
                reviews.isLast()
        );
        ApiResponse<PagedResponse<ReviewResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Reviews fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get reviews by user")
    @RequiresRole(UserRole.CUSTOMER)
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponseDTO>>> getReviewsByUser(
            HttpServletRequest httpRequest,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Long userId = (Long) httpRequest.getAttribute("authUserId");
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<ReviewResponseDTO> reviews = reviewService.getReviewsByUserId(userId, pageable);
        PagedResponse<ReviewResponseDTO> pagedResponse = new PagedResponse<>(
                reviews.getContent(),
                reviews.getNumber(),
                (int) reviews.getTotalElements(),
                reviews.getTotalPages(),
                reviews.isLast()
        );
        ApiResponse<PagedResponse<ReviewResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Reviews fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }
}
