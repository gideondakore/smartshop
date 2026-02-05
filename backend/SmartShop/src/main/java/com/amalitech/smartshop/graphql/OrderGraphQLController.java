package com.amalitech.smartshop.graphql;

import com.amalitech.smartshop.config.GraphQLRequiresRole;
import com.amalitech.smartshop.dtos.requests.AddOrderDTO;
import com.amalitech.smartshop.dtos.requests.OrderItemDTO;
import com.amalitech.smartshop.dtos.requests.UpdateOrderDTO;
import com.amalitech.smartshop.dtos.responses.OrderResponseDTO;
import com.amalitech.smartshop.enums.OrderStatus;
import com.amalitech.smartshop.enums.UserRole;
import com.amalitech.smartshop.interfaces.OrderService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GraphQL controller for order-related queries and mutations.
 */
@Controller
@RequiredArgsConstructor
public class OrderGraphQLController {

    private final OrderService orderService;

    @QueryMapping
    @GraphQLRequiresRole(UserRole.ADMIN)
    public List<OrderResponseDTO> allOrders(DataFetchingEnvironment env) {
        return orderService.getAllOrders(Pageable.unpaged()).getContent();
    }

    @QueryMapping
    @GraphQLRequiresRole({UserRole.ADMIN, UserRole.CUSTOMER})
    public OrderResponseDTO orderById(@Argument Long id, DataFetchingEnvironment env) {
        return orderService.getOrderById(id);
    }

    @QueryMapping
    @GraphQLRequiresRole({UserRole.ADMIN, UserRole.CUSTOMER})
    public List<OrderResponseDTO> ordersByUserId(@Argument Long userId, DataFetchingEnvironment env) {
        return orderService.getOrdersByUserId(userId, Pageable.unpaged()).getContent();
    }

    @MutationMapping
    @GraphQLRequiresRole({UserRole.ADMIN, UserRole.CUSTOMER})
    public OrderResponseDTO createOrder(@Argument AddOrderInput input, DataFetchingEnvironment env) {
        AddOrderDTO dto = new AddOrderDTO();
        dto.setUserId(input.userId());
        dto.setItems(input.items().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setProductId(item.productId());
                    itemDTO.setQuantity(item.quantity());
                    return itemDTO;
                })
                .collect(Collectors.toList()));
        return orderService.createOrder(dto);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.ADMIN)
    public OrderResponseDTO updateOrderStatus(@Argument Long id, @Argument UpdateOrderInput input, DataFetchingEnvironment env) {
        UpdateOrderDTO dto = new UpdateOrderDTO();
        dto.setStatus(input.status());
        return orderService.updateOrderStatus(id, dto);
    }

    @MutationMapping
    @GraphQLRequiresRole(UserRole.ADMIN)
    public boolean deleteOrder(@Argument Long id, DataFetchingEnvironment env) {
        orderService.deleteOrder(id);
        return true;
    }

    public record AddOrderInput(Long userId, List<OrderItemInput> items) {}

    public record OrderItemInput(Long productId, Integer quantity) {}

    public record UpdateOrderInput(OrderStatus status) {}
}
