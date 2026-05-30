package com.hometown.shots.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(
        @NotBlank String text,
        Long parentId
) {}
