package com.xrpshield.controller;

import com.xrpshield.dto.ApiResponse;
import com.xrpshield.dto.AuthResponseDto;
import com.xrpshield.dto.LoginRequestDto;
import com.xrpshield.dto.RefreshTokenRequestDto;
import com.xrpshield.dto.RegisterRequestDto;
import com.xrpshield.dto.UserProfileResponseDto;
import com.xrpshield.entity.UserEntity;
import com.xrpshield.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication & User Identity", description = "Endpoints for registration, login, JWT token refresh, profile, and session management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register New User", description = "Creates a new user account with BCrypt password hashing")
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        AuthResponseDto response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "User Password Login", description = "Authenticates email/password credentials and issues JWT Access & Refresh Tokens")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Access Token", description = "Exchanges a valid refresh token for a new JWT access token")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        AuthResponseDto response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Invalidates active user session")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logout successful", "Session terminated"));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get User Profile", description = "Retrieves authenticated user profile details, roles, and linked wallets")
    public ResponseEntity<ApiResponse<UserProfileResponseDto>> getProfile(@AuthenticationPrincipal UserEntity user) {
        UserProfileResponseDto profile = authService.getUserProfile(user.getId());
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved", profile));
    }
}
