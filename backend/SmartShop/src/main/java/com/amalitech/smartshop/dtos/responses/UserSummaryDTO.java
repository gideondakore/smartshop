package com.amalitech.smartshop.dtos.responses;

import com.amalitech.smartshop.enums.UserRole;
import lombok.Data;

@Data
public class UserSummaryDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String name;
    private String email;
    private UserRole role;
}
