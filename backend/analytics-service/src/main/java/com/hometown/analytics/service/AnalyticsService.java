package com.hometown.analytics.service;

import com.hometown.analytics.domain.EventType;
import com.hometown.analytics.domain.VisitEvent;
import com.hometown.analytics.dto.SellerSummaryResponse;
import com.hometown.analytics.dto.TopProductDto;
import com.hometown.analytics.dto.VisitEventRequest;
import com.hometown.analytics.repo.ProductViewCount;
import com.hometown.analytics.repo.VisitEventRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnalyticsService {

    private final VisitEventRepository repository;
    private final MeterRegistry meterRegistry;

    public AnalyticsService(VisitEventRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }

    @Transactional
    public void ingest(VisitEventRequest req) {
        VisitEvent event = new VisitEvent();
        event.setType(req.type());
        event.setProductId(req.productId());
        event.setSellerId(req.sellerId());
        event.setCategory(req.category());
        event.setUserId(req.userId());
        event.setGuest(req.guest());
        event.setSessionId(req.sessionId());
        event.setReferrer(req.referrer());
        repository.save(event);

        meterRegistry.counter("hometown_visit_events_total", "type", req.type().name()).increment();

        if (req.type() == EventType.PRODUCT_VIEW) {
            meterRegistry.counter(
                    "hometown_product_views_total",
                    "product", req.productId() != null ? req.productId().toString() : "unknown",
                    "seller", req.sellerId() != null ? req.sellerId().toString() : "unknown",
                    "category", req.category() != null ? req.category() : "unknown"
            ).increment();
        }
    }

    @Transactional(readOnly = true)
    public SellerSummaryResponse sellerSummary(Long sellerId) {
        long totalVisits = repository.countBySellerId(sellerId);
        long productViews = repository.countBySellerIdAndType(sellerId, EventType.PRODUCT_VIEW);
        long addToCarts = repository.countBySellerIdAndType(sellerId, EventType.ADD_TO_CART);
        long checkouts = repository.countBySellerIdAndType(sellerId, EventType.CHECKOUT);
        long uniqueSessions = repository.countDistinctSessionsBySellerId(sellerId);
        return new SellerSummaryResponse(totalVisits, productViews, addToCarts, checkouts, uniqueSessions);
    }

    @Transactional(readOnly = true)
    public List<TopProductDto> topProducts(int limit) {
        List<ProductViewCount> results = repository.findTopProducts();
        return results.stream()
                .limit(limit)
                .map(r -> new TopProductDto(r.getProductId(), r.getViews()))
                .toList();
    }
}
