package com.hometown.audit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuditInterceptor implements HandlerInterceptor {

    private final RequestLogRepository repo;

    public AuditInterceptor(RequestLogRepository repo) {
        this.repo = repo;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("auditStart", System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String path = request.getRequestURI();
        if (path == null || !path.startsWith("/api/")
                || path.startsWith("/api/audit")
                || path.startsWith("/api/catalog")
                || path.startsWith("/api/images")) {
            return;
        }
        try {
            Object startAttr = request.getAttribute("auditStart");
            long ms = startAttr == null ? 0 : (System.nanoTime() - (long) startAttr) / 1_000_000;

            Long userId = null;
            String role = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
                try { userId = Long.valueOf(auth.getName()); } catch (NumberFormatException ignored) { }
                role = auth.getAuthorities().stream().findFirst()
                        .map(a -> a.getAuthority().replace("ROLE_", "")).orElse(null);
            }

            RequestLog log = new RequestLog();
            log.setMethod(request.getMethod());
            log.setPath(path);
            log.setStatus(response.getStatus());
            log.setUserId(userId);
            log.setRole(role);
            log.setDurationMs(ms);
            repo.save(log);
        } catch (Exception ignored) {
            // auditing must never break the request
        }
    }
}
