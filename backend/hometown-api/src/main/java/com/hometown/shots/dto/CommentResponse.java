package com.hometown.shots.dto;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long shotId,
        String userName,
        Long parentId,
        String text,
        Instant createdAt
) {}
