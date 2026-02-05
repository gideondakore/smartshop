package com.amalitech.smartshop.graphql;

import com.amalitech.smartshop.config.GraphQLRequiresRole;
import com.amalitech.smartshop.dtos.requests.AddProductDTO;
import com.amalitech.smartshop.dtos.responses.ProductResponseDTO;
import com.amalitech.smartshop.enums.UserRole;
import com.amalitech.smartshop.interfaces.ProductService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL controller for product-related queries and mutations.
 */
@Controller
@RequiredArgsConstructor
public class ProductGraphQLController {

    private final ProductService productService;

    @QueryMapping
    public List<ProductResponseDTO> allProducts() {
        return productService.getAllProductsList();
    }

    @QueryMapping
    public ProductResponseDTO productById(@Argument Long id) {
        return productService.getProductById(id);
    }

    @MutationMapping
    @GraphQLRequiresRole({UserRole.ADMIN, UserRole.VENDOR})
    public ProductResponseDTO addProduct(@Argument AddProductInput input, DataFetchingEnvironment env) {
        AddProductDTO dto = new AddProductDTO();
        dto.setName(input.name());
        dto.setCategoryId(input.categoryId());
        dto.setSku(input.sku());
        dto.setPrice(input.price());
        
        // Get user context from GraphQL
        Long userId = env.getGraphQlContext().get("userId");
        String userRole = env.getGraphQlContext().get("userRole");
        
        return productService.addProduct(dto, userId, userRole);
    }

    @MutationMapping
    @GraphQLRequiresRole({UserRole.ADMIN, UserRole.VENDOR})
    public boolean deleteProduct(@Argument Long id, DataFetchingEnvironment env) {
        productService.deleteProduct(id);
        return true;
    }

    public record AddProductInput(String name, Long categoryId, String sku, Double price) {}
}
