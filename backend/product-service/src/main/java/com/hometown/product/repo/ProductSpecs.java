package com.hometown.product.repo;

import com.hometown.product.domain.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecs {

    private ProductSpecs() {}

    public static Specification<Product> categoryIs(Long id) {
        if (id == null) return null;
        return (root, query, cb) -> cb.equal(root.get("categoryId"), id);
    }

    public static Specification<Product> nameContains(String q) {
        if (q == null || q.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + q.toLowerCase() + "%");
    }

    public static Specification<Product> priceGte(BigDecimal min) {
        if (min == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    public static Specification<Product> priceLte(BigDecimal max) {
        if (max == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
    }

    public static Specification<Product> inStock() {
        return (root, query, cb) -> cb.greaterThan(root.get("stock"), 0);
    }
}
