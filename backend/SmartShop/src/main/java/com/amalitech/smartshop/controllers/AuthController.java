package com.amalitech.smartshop.controllers;

import com.amalitech.smartshop.dtos.responses.ApiResponse;
import com.amalitech.smartshop.interfaces.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "APIs for authentication operations")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SessionService sessionService;

    @Operation(summary = "Logout user")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String token = (String) request.getAttribute("sessionToken");
        if (token != null) {
            sessionService.deleteSession(token);
        }
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Logged out successfully", null);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Logout from all devices")
    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAll(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        sessionService.deleteAllUserSessions(userId);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Logged out from all devices", null);
        return ResponseEntity.ok(apiResponse);
    }
}
