package com.hometown.product.service;

import com.hometown.common.web.ApiException;
import com.hometown.product.domain.ProductReview;
import com.hometown.product.dto.ReviewRequest;
import com.hometown.product.dto.ReviewResponse;
import com.hometown.product.repo.ProductRepository;
import com.hometown.product.repo.ProductReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ProductReviewRepository reviewRepo;
    private final ProductRepository productRepo;

    public ReviewService(ProductReviewRepository reviewRepo, ProductRepository productRepo) {
        this.reviewRepo = reviewRepo;
        this.productRepo = productRepo;
    }

    @Transactional
    public ReviewResponse addOrUpdate(Long productId, Long userId, String reviewerName, ReviewRequest req) {
        if (!productRepo.existsById(productId)) {
            throw ApiException.notFound("Product not found: " + productId);
        }
        ProductReview review = reviewRepo.findByProductIdAndUserId(productId, userId)
                .orElseGet(() -> {
                    ProductReview r = new ProductReview();
                    r.setProductId(productId);
                    r.setUserId(userId);
                    return r;
                });
        review.setReviewerName(reviewerName);
        review.setRating(req.rating());
        review.setComment(req.comment());
        ProductReview saved = reviewRepo.save(review);
        return toResponse(saved);
    }

    public List<ReviewResponse> list(Long productId) {
        return reviewRepo.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReviewResponse toResponse(ProductReview r) {
        return new ReviewResponse(r.getId(), r.getProductId(), r.getReviewerName(),
                r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
