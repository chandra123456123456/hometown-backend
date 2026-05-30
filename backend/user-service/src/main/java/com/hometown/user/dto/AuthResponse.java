package com.hometown.user.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String name,
        String email,
        String role
) {}
