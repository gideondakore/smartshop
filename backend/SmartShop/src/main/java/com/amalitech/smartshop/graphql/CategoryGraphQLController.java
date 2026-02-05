package com.amalitech.smartshop.graphql;

import com.amalitech.smartshop.config.GraphQLRequiresRole;
import com.amalitech.smartshop.dtos.requests.AddCategoryDTO;
import com.amalitech.smartshop.dtos.requests.UpdateCategoryDTO;
import com.amalitech.smartshop.dtos.responses.CategoryResponseDTO;
import com.amalitech.smartshop.enums.UserRole;
import com.amalitech.smartshop.interfaces.CategoryService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL controller for category-related queries and mutations.
 */
@Controller
@RequiredArgsConstructor
public class CategoryGraphQLController {

    private final CategoryService categoryService;

    @QueryMapping
    public List<CategoryResponseDTO> allCategories() {
        return categoryService.getAllCategories(Pageable.unpaged()).getContent();
    }

    @QueryMapping
    public CategoryResponseDTO categoryById(@Argument Long id) {
        return categoryService.getCategoryById(id);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.ADMIN)
    public CategoryResponseDTO addCategory(@Argument AddCategoryInput input, DataFetchingEnvironment env) {
        AddCategoryDTO dto = new AddCategoryDTO();
        dto.setName(input.name());
        dto.setDescription(input.description());
        return categoryService.addCategory(dto);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.ADMIN)
    public CategoryResponseDTO updateCategory(@Argument Long id, @Argument UpdateCategoryInput input, DataFetchingEnvironment env) {
        UpdateCategoryDTO dto = new UpdateCategoryDTO();
        dto.setName(input.name());
        dto.setDescription(input.description());
        return categoryService.updateCategory(id, dto);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.ADMIN)
    public boolean deleteCategory(@Argument Long id, DataFetchingEnvironment env) {
        categoryService.deleteCategory(id);
        return true;
    }

    public record AddCategoryInput(String name, String description) {}

    public record UpdateCategoryInput(String name, String description) {}
}
