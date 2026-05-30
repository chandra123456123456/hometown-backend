package com.hometown.product.repo;

import com.hometown.product.domain.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId);

    Optional<ProductReview> findByProductIdAndUserId(Long productId, Long userId);

    @Query("select coalesce(avg(r.rating), 0) from ProductReview r where r.productId = :pid")
    double avgRating(@Param("pid") Long pid);

    long countByProductId(Long productId);
}
