package com.hometown.shots.dto;

import java.time.Instant;

public record ShotResponse(
        Long id,
        String title,
        String caption,
        String videoUrl,
        Long uploadedBy,
        Instant createdAt,
        long likeCount,
        boolean likedByMe,
        long commentCount
) {}
