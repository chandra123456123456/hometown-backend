package com.hometown.product.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        schema = "products",
        name = "product_reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "user_id"})
)
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String reviewerName;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "text")
    private String comment;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ProductReview() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
