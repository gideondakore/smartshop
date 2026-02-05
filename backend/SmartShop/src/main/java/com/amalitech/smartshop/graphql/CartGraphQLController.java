package com.amalitech.smartshop.graphql;

import com.amalitech.smartshop.config.GraphQLRequiresRole;
import com.amalitech.smartshop.dtos.requests.AddCartItemDTO;
import com.amalitech.smartshop.dtos.requests.UpdateCartItemDTO;
import com.amalitech.smartshop.dtos.responses.CartResponseDTO;
import com.amalitech.smartshop.dtos.responses.OrderResponseDTO;
import com.amalitech.smartshop.enums.UserRole;
import com.amalitech.smartshop.interfaces.CartService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CartGraphQLController {
    
    private final CartService cartService;

    @QueryMapping
    @GraphQLRequiresRole(UserRole.CUSTOMER)
    public CartResponseDTO getCart(DataFetchingEnvironment env) {
        Long userId = (Long) env.getGraphQlContext().get("userId");
        return cartService.getCartByUserId(userId);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.CUSTOMER)
    public CartResponseDTO addItemToCart(@Argument AddCartItemInput input, DataFetchingEnvironment env) {
        Long userId = (Long) env.getGraphQlContext().get("userId");
        AddCartItemDTO dto = new AddCartItemDTO();
        dto.setProductId(input.productId());
        dto.setQuantity(input.quantity());
        return cartService.addItemToCart(dto, userId);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.CUSTOMER)
    public CartResponseDTO updateCartItem(@Argument Long itemId, @Argument UpdateCartItemInput input, DataFetchingEnvironment env) {
        Long userId = (Long) env.getGraphQlContext().get("userId");
        UpdateCartItemDTO dto = new UpdateCartItemDTO();
        dto.setQuantity(input.quantity());
        return cartService.updateCartItem(itemId, dto, userId);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.CUSTOMER)
    public CartResponseDTO removeItemFromCart(@Argument Long itemId, DataFetchingEnvironment env) {
        Long userId = (Long) env.getGraphQlContext().get("userId");
        return cartService.removeItemFromCart(itemId, userId);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.CUSTOMER)
    public boolean clearCart(DataFetchingEnvironment env) {
        Long userId = (Long) env.getGraphQlContext().get("userId");
        cartService.clearCart(userId);
        return true;
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.CUSTOMER)
    public CartResponseDTO checkoutCart(DataFetchingEnvironment env) {
        Long userId = (Long) env.getGraphQlContext().get("userId");
        return cartService.checkoutCart(userId);
    }

    public record AddCartItemInput(Long productId, Integer quantity) {}
    public record UpdateCartItemInput(Integer quantity) {}
}
