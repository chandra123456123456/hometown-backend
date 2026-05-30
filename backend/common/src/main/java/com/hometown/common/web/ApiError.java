package com.hometown.common.web;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Uniform error body returned by all HomeTown services.
 */
public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details
) {
    public static ApiError of(int status, String error, String message, String path, List<String> details) {
        return new ApiError(OffsetDateTime.now(), status, error, message, path, details);
    }
}
