package com.amalitech.smartshop.graphql;

import com.amalitech.smartshop.config.GraphQLRequiresRole;
import com.amalitech.smartshop.dtos.requests.LoginDTO;
import com.amalitech.smartshop.dtos.requests.UserRegistrationDTO;
import com.amalitech.smartshop.dtos.responses.LoginResponseDTO;
import com.amalitech.smartshop.dtos.responses.UserSummaryDTO;
import com.amalitech.smartshop.enums.UserRole;
import com.amalitech.smartshop.interfaces.UserService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL resolver for user-related queries and mutations.
 */
@Controller
@RequiredArgsConstructor
public class UserResolver {

    private final UserService userService;

    @QueryMapping
    @GraphQLRequiresRole(UserRole.ADMIN)
    public List<UserSummaryDTO> getAllUsers(DataFetchingEnvironment env) {
        return userService.getAllUsers(Pageable.unpaged()).getContent();
    }

    @MutationMapping
    public AuthResponse login(@Argument LoginInput input) {
        LoginDTO dto = new LoginDTO();
        dto.setEmail(input.email());
        dto.setPassword(input.password());
        LoginResponseDTO response = userService.loginUser(dto);
        return new AuthResponse(response.getToken(), response.getId(), response.getEmail(),
                response.getFirstName(), response.getLastName(), response.getRole());
    }

    @MutationMapping
    public AuthResponse register(@Argument RegisterInput input) {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setFirstName(input.firstName());
        dto.setLastName(input.lastName());
        dto.setEmail(input.email());
        dto.setPassword(input.password());
        dto.setRole(input.role());
        LoginResponseDTO response = userService.addUser(dto);
        return new AuthResponse(response.getToken(), response.getId(), response.getEmail(),
                response.getFirstName(), response.getLastName(), response.getRole());
    }

    public record LoginInput(String email, String password) {}

    public record RegisterInput(String firstName, String lastName, String email, String password, UserRole role) {}

    public record AuthResponse(String token, Long id, String email, String firstName, String lastName, String role) {}
}
