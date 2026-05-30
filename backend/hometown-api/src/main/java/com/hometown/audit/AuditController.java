package com.hometown.audit;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuditController {

    private final RequestLogRepository repo;

    public AuditController(RequestLogRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/audit/requests")
    @PreAuthorize("hasRole('DEVELOPER')")
    public List<RequestLogDto> requests(@RequestParam(required = false) String path,
                                        @RequestParam(required = false) Integer status,
                                        @RequestParam(defaultValue = "200") int limit) {
        var page = PageRequest.of(0, Math.min(limit, 500));
        List<RequestLog> logs = (path != null && !path.isBlank())
                ? repo.findByPathContainingIgnoreCaseOrderByIdDesc(path, page)
                : repo.findByOrderByIdDesc(page);
        return logs.stream()
                .filter(r -> status == null || r.getStatus() == status)
                .map(RequestLogDto::of)
                .toList();
    }
}
