package com.hometown.common.security;

import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {
    }

    public static Long id() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user");
        }
        return Long.valueOf(auth.getName());
    }

    public static String email() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        Object details = auth == null ? null : auth.getDetails();
        return details instanceof String s ? s : null;
    }

    public static boolean isAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Roles.ROLE_ADMIN));
    }
}
