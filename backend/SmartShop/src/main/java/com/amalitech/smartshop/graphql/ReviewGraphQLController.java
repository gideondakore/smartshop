package com.amalitech.smartshop.graphql;

import com.amalitech.smartshop.config.GraphQLRequiresRole;
import com.amalitech.smartshop.dtos.requests.AddReviewDTO;
import com.amalitech.smartshop.dtos.requests.UpdateReviewDTO;
import com.amalitech.smartshop.dtos.responses.ReviewResponseDTO;
import com.amalitech.smartshop.enums.UserRole;
import com.amalitech.smartshop.interfaces.ReviewService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewGraphQLController {
    
    private final ReviewService reviewService;

    @QueryMapping
    public List<ReviewResponseDTO> allReviews() {
        return reviewService.getAllReviews(Pageable.unpaged()).getContent();
    }

    @QueryMapping
    public ReviewResponseDTO reviewById(@Argument Long id) {
        return reviewService.getReviewById(id);
    }

    @QueryMapping
    public List<ReviewResponseDTO> reviewsByProductId(@Argument Long productId) {
        return reviewService.getReviewsByProductId(productId, Pageable.unpaged()).getContent();
    }

    @QueryMapping
    @GraphQLRequiresRole(UserRole.CUSTOMER)
    public List<ReviewResponseDTO> reviewsByUserId(@Argument Long userId, DataFetchingEnvironment env) {
        return reviewService.getReviewsByUserId(userId, Pageable.unpaged()).getContent();
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.CUSTOMER)
    public ReviewResponseDTO addReview(@Argument AddReviewInput input, DataFetchingEnvironment env) {
        Long userId = (Long) env.getGraphQlContext().get("userId");
        AddReviewDTO dto = new AddReviewDTO();
        dto.setProductId(input.productId());
        dto.setRating(input.rating());
        dto.setComment(input.comment());
        return reviewService.addReview(dto, userId);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.CUSTOMER)
    public ReviewResponseDTO updateReview(@Argument Long id, @Argument UpdateReviewInput input, DataFetchingEnvironment env) {
        Long userId = (Long) env.getGraphQlContext().get("userId");
        UpdateReviewDTO dto = new UpdateReviewDTO();
        dto.setRating(input.rating());
        dto.setComment(input.comment());
        return reviewService.updateReview(id, dto, userId);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.CUSTOMER)
    public boolean deleteReview(@Argument Long id, DataFetchingEnvironment env) {
        Long userId = (Long) env.getGraphQlContext().get("userId");
        reviewService.deleteReview(id, userId);
        return true;
    }

    public record AddReviewInput(Long productId, Integer rating, String comment) {}
    public record UpdateReviewInput(Integer rating, String comment) {}
}
