package com.hometown.product.web;

import com.hometown.common.security.CurrentUser;
import com.hometown.product.dto.ReviewRequest;
import com.hometown.product.dto.ReviewResponse;
import com.hometown.product.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/api/products/{id}/reviews")
    public List<ReviewResponse> list(@PathVariable Long id) {
        return reviewService.list(id);
    }

    @PostMapping("/api/products/{id}/reviews")
    public ResponseEntity<ReviewResponse> addOrUpdate(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest req) {
        Long userId = CurrentUser.id();
        String email = CurrentUser.email();
        String name = (email != null && email.contains("@"))
                ? email.substring(0, email.indexOf('@'))
                : "User";
        ReviewResponse response = reviewService.addOrUpdate(id, userId, name, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
