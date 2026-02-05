package com.amalitech.smartshop.graphql;

import com.amalitech.smartshop.config.GraphQLRequiresRole;
import com.amalitech.smartshop.dtos.requests.AddInventoryDTO;
import com.amalitech.smartshop.dtos.requests.UpdateInventoryDTO;
import com.amalitech.smartshop.dtos.responses.InventoryResponseDTO;
import com.amalitech.smartshop.enums.UserRole;
import com.amalitech.smartshop.interfaces.InventoryService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL controller for inventory-related queries and mutations.
 */
@Controller
@RequiredArgsConstructor
public class InventoryGraphQLController {

    private final InventoryService inventoryService;

    @QueryMapping
    @GraphQLRequiresRole({UserRole.ADMIN, UserRole.VENDOR})
    public List<InventoryResponseDTO> allInventories(DataFetchingEnvironment env) {
        return inventoryService.getAllInventories(Pageable.unpaged()).getContent();
    }

    @QueryMapping
    @GraphQLRequiresRole({UserRole.ADMIN, UserRole.VENDOR})
    public InventoryResponseDTO inventoryById(@Argument Long id, DataFetchingEnvironment env) {
        return inventoryService.getInventoryById(id);
    }

    @QueryMapping
    @GraphQLRequiresRole({UserRole.ADMIN, UserRole.VENDOR})
    public InventoryResponseDTO inventoryByProductId(@Argument Long productId, DataFetchingEnvironment env) {
        return inventoryService.getInventoryByProductId(productId);
    }

    @MutationMapping
    @GraphQLRequiresRole({UserRole.ADMIN, UserRole.VENDOR})
    public InventoryResponseDTO addInventory(@Argument AddInventoryInput input, DataFetchingEnvironment env) {
        AddInventoryDTO dto = new AddInventoryDTO();
        dto.setProductId(input.productId());
        dto.setQuantity(input.quantity());
        dto.setLocation(input.location());
        return inventoryService.addInventory(dto);
    }

    @MutationMapping
    @GraphQLRequiresRole({UserRole.ADMIN, UserRole.VENDOR})
    public InventoryResponseDTO updateInventory(@Argument Long id, @Argument UpdateInventoryInput input, DataFetchingEnvironment env) {
        UpdateInventoryDTO dto = new UpdateInventoryDTO();
        dto.setQuantity(input.quantity());
        dto.setLocation(input.location());
        return inventoryService.updateInventory(id, dto);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.ADMIN)
    public boolean deleteInventory(@Argument Long id, DataFetchingEnvironment env) {
        inventoryService.deleteInventory(id);
        return true;
    }

    public record AddInventoryInput(Long productId, Integer quantity, String location) {}

    public record UpdateInventoryInput(Integer quantity, String location) {}
}
