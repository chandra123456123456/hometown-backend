package com.hometown.product.service;

import com.hometown.common.web.ApiException;
import com.hometown.product.domain.Product;
import com.hometown.product.dto.ProductRequest;
import com.hometown.product.dto.ProductResponse;
import com.hometown.product.image.ImageUrlSigner;
import com.hometown.product.repo.ProductRepository;
import com.hometown.product.repo.ProductReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repo;
    private final ProductReviewRepository reviewRepo;
    private final ImageUrlSigner signer;

    public ProductService(ProductRepository repo, ProductReviewRepository reviewRepo, ImageUrlSigner signer) {
        this.repo = repo;
        this.reviewRepo = reviewRepo;
        this.signer = signer;
    }

    private ProductResponse toResponse(Product p) {
        ProductResponse r = ProductResponse.of(p);
        List<String> signed = r.imageUrls() == null ? List.of()
                : r.imageUrls().stream().map(signer::sign).toList();
        double avg = reviewRepo.avgRating(p.getId());
        double rounded = BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP).doubleValue();
        int count = (int) reviewRepo.countByProductId(p.getId());
        return new ProductResponse(r.id(), r.name(), r.description(), r.price(),
                r.discountPercent(), r.effectivePrice(), r.categoryId(), r.stock(),
                r.active(), r.sellerId(), r.createdAt(), signed, rounded, count);
    }

    public Page<ProductResponse> search(Long categoryId, String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> results;
        if (categoryId != null && q != null && !q.isBlank()) {
            results = repo.findByCategoryIdAndNameContainingIgnoreCase(categoryId, q, pageable);
        } else if (categoryId != null) {
            results = repo.findByCategoryId(categoryId, pageable);
        } else if (q != null && !q.isBlank()) {
            results = repo.findByNameContainingIgnoreCase(q, pageable);
        } else {
            results = repo.findAll(pageable);
        }
        return results.map(this::toResponse);
    }

    public ProductResponse findById(Long id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + id));
        return toResponse(p);
    }

    @Transactional
    public ProductResponse create(ProductRequest req) {
        Product p = applyRequest(new Product(), req);
        return toResponse(repo.save(p));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest req) {
        Product p = repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + id));
        return toResponse(repo.save(applyRequest(p, req)));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw ApiException.notFound("Product not found: " + id);
        }
        repo.deleteById(id);
    }

    private Product applyRequest(Product p, ProductRequest req) {
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setDiscountPercent(req.discountPercent());
        p.setCategoryId(req.categoryId());
        p.setStock(req.stock());
        p.setActive(req.active());
        p.setSellerId(req.sellerId());
        List<String> urls = new ArrayList<>();
        if (req.imageUrls() != null) {
            for (String u : req.imageUrls()) urls.add(signer.stripQuery(u));
        }
        p.setImageUrls(urls);
        return p;
    }
}
