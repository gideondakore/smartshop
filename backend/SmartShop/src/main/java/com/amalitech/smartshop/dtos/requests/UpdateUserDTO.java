package com.amalitech.smartshop.dtos.requests;

import com.amalitech.smartshop.enums.UserRole;
import lombok.Data;

@Data
public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
}

