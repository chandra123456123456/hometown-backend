package com.hometown.product.service;

import com.hometown.common.web.ApiException;
import com.hometown.product.domain.Product;
import com.hometown.product.dto.ProductRequest;
import com.hometown.product.dto.ProductResponse;
import com.hometown.product.repo.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
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
        return results.map(ProductResponse::of);
    }

    public ProductResponse findById(Long id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + id));
        return ProductResponse.of(p);
    }

    @Transactional
    public ProductResponse create(ProductRequest req) {
        Product p = applyRequest(new Product(), req);
        return ProductResponse.of(repo.save(p));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest req) {
        Product p = repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + id));
        return ProductResponse.of(repo.save(applyRequest(p, req)));
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
        p.setImageUrls(req.imageUrls() != null ? new ArrayList<>(req.imageUrls()) : new ArrayList<>());
        return p;
    }
}
