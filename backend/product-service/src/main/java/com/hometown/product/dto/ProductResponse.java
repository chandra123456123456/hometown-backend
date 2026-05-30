package com.hometown.product.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int discountPercent,
        BigDecimal effectivePrice,
        Long categoryId,
        int stock,
        boolean active,
        Long sellerId,
        Instant createdAt,
        List<String> imageUrls,
        Double avgRating,
        Integer reviewCount,
        boolean antique,
        String artType,
        boolean framable,
        Integer artWidthCm,
        Integer artHeightCm
) {
    public static ProductResponse of(com.hometown.product.domain.Product p) {
        BigDecimal effective = p.getDiscountPercent() == 0
                ? p.getPrice()
                : p.getPrice()
                        .multiply(BigDecimal.valueOf(100 - p.getDiscountPercent()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        String art = p.getArtType() == null ? "NONE" : p.getArtType();
        boolean framable = "SKETCH".equals(art) || "PAINTING".equals(art) || "CANVAS".equals(art);
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getDiscountPercent(),
                effective,
                p.getCategoryId(),
                p.getStock(),
                p.isActive(),
                p.getSellerId(),
                p.getCreatedAt(),
                p.getImageUrls(),
                null,
                0,
                p.isAntique(),
                art,
                framable,
                p.getArtWidthCm(),
                p.getArtHeightCm()
        );
    }
}
