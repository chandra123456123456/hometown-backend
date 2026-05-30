package com.hometown.product.util;

import com.hometown.product.domain.Product;
import com.hometown.product.dto.FrameOption;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FramePricing {

    public enum FrameType {
        NONE, PLASTIC, WOODEN, WOODEN_COLOR, PREMIUM_WOODEN
    }

    private static final Map<FrameType, BigDecimal> BASE = Map.of(
            FrameType.NONE, BigDecimal.ZERO,
            FrameType.PLASTIC, new BigDecimal("199"),
            FrameType.WOODEN, new BigDecimal("499"),
            FrameType.WOODEN_COLOR, new BigDecimal("749"),
            FrameType.PREMIUM_WOODEN, new BigDecimal("1199")
    );

    private static final Map<FrameType, String> LABEL = Map.of(
            FrameType.NONE, "No frame",
            FrameType.PLASTIC, "Plastic frame",
            FrameType.WOODEN, "Wooden frame",
            FrameType.WOODEN_COLOR, "Wooden frame (colored)",
            FrameType.PREMIUM_WOODEN, "Premium wooden frame"
    );

    private FramePricing() {}

    private static double factor(int area) {
        if (area <= 600) return 1.0;
        if (area <= 1200) return 1.3;
        if (area <= 2400) return 1.7;
        return 2.2;
    }

    private static int effectiveArea(int w, int h) {
        return (w <= 0 || h <= 0) ? 1200 : w * h;
    }

    public static BigDecimal charge(String frameTypeStr, int artWidthCm, int artHeightCm) {
        if (frameTypeStr == null) return BigDecimal.ZERO;
        FrameType type;
        try {
            type = FrameType.valueOf(frameTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BigDecimal.ZERO;
        }
        if (type == FrameType.NONE) return BigDecimal.ZERO;
        int area = effectiveArea(artWidthCm, artHeightCm);
        BigDecimal base = BASE.get(type);
        BigDecimal factor = BigDecimal.valueOf(factor(area));
        return base.multiply(factor).setScale(0, RoundingMode.HALF_UP);
    }

    public static List<FrameOption> optionsFor(Product p) {
        String art = p.getArtType() == null ? "NONE" : p.getArtType();
        boolean framable = "SKETCH".equals(art) || "PAINTING".equals(art) || "CANVAS".equals(art);
        if (!framable) return List.of();
        int area = effectiveArea(p.getArtWidthCm(), p.getArtHeightCm());
        List<FrameOption> options = new ArrayList<>();
        for (FrameType t : FrameType.values()) {
            BigDecimal price;
            if (t == FrameType.NONE) {
                price = BigDecimal.ZERO;
            } else {
                price = BASE.get(t).multiply(BigDecimal.valueOf(factor(area)))
                        .setScale(0, RoundingMode.HALF_UP);
            }
            options.add(new FrameOption(t.name(), LABEL.get(t), price));
        }
        return options;
    }
}
