package com.hometown.analytics.repo;

import com.hometown.analytics.domain.EventType;
import com.hometown.analytics.domain.VisitEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitEventRepository extends JpaRepository<VisitEvent, Long> {

    long countByType(EventType type);

    long countBySellerId(Long sellerId);

    @Query("SELECT v.productId AS productId, COUNT(v) AS views " +
           "FROM VisitEvent v " +
           "WHERE v.type = com.hometown.analytics.domain.EventType.PRODUCT_VIEW " +
           "GROUP BY v.productId " +
           "ORDER BY COUNT(v) DESC")
    List<ProductViewCount> findTopProducts();

    @Query("SELECT COUNT(DISTINCT v.sessionId) FROM VisitEvent v WHERE v.sellerId = :sellerId")
    long countDistinctSessionsBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(v) FROM VisitEvent v WHERE v.sellerId = :sellerId AND v.type = :type")
    long countBySellerIdAndType(@Param("sellerId") Long sellerId, @Param("type") EventType type);
}
