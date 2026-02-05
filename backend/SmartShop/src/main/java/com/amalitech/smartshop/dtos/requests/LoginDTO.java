package com.amalitech.smartshop.dtos.requests;

import lombok.Data;

@Data
public class LoginDTO {
    private String email;
    private String password;
}
