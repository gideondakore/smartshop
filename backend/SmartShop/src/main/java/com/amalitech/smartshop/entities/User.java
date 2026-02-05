package com.amalitech.smartshop.entities;

import com.amalitech.smartshop.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a user in the SmartShop e-commerce system.
 * Users can have different roles: ADMIN, VENDOR, or CUSTOMER.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserRole role;

    /**
     * Returns the full name of the user.
     *
     * @return concatenation of first and last name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
