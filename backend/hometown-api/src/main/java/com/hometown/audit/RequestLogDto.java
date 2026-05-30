package com.hometown.audit;

import java.time.Instant;

public record RequestLogDto(
        Long id,
        String method,
        String path,
        int status,
        Long userId,
        String role,
        long durationMs,
        Instant createdAt
) {
    static RequestLogDto of(RequestLog r) {
        return new RequestLogDto(r.getId(), r.getMethod(), r.getPath(), r.getStatus(),
                r.getUserId(), r.getRole(), r.getDurationMs(), r.getCreatedAt());
    }
}
