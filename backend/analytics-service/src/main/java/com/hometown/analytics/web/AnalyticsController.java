package com.hometown.analytics.web;

import com.hometown.analytics.dto.SellerSummaryResponse;
import com.hometown.analytics.dto.TopProductDto;
import com.hometown.analytics.dto.VisitEventRequest;
import com.hometown.analytics.service.AnalyticsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/events")
    public ResponseEntity<Void> ingest(@Valid @RequestBody VisitEventRequest request) {
        analyticsService.ingest(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/seller/{sellerId}/summary")
    public ResponseEntity<SellerSummaryResponse> sellerSummary(@PathVariable Long sellerId) {
        return ResponseEntity.ok(analyticsService.sellerSummary(sellerId));
    }

    @GetMapping("/products/top")
    public ResponseEntity<List<TopProductDto>> topProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.topProducts(limit));
    }
}
