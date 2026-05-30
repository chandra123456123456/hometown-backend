package com.hometown.user.dto;

public record UserResponse(
        Long id,
        String name,
        String email,
        String role
) {}
