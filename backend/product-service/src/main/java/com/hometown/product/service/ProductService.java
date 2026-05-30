package com.hometown.product.service;

import com.hometown.common.web.ApiException;
import com.hometown.product.domain.Product;
import com.hometown.product.dto.FrameOption;
import com.hometown.product.dto.ProductOrderInfo;
import com.hometown.product.dto.ProductRequest;
import com.hometown.product.dto.ProductResponse;
import com.hometown.product.image.ImageUrlSigner;
import com.hometown.product.repo.ProductRepository;
import com.hometown.product.repo.ProductReviewRepository;
import com.hometown.product.repo.ProductSpecs;
import com.hometown.product.util.FramePricing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
                r.active(), r.sellerId(), r.createdAt(), signed, rounded, count, r.antique(),
                r.artType(), r.framable(), r.artWidthCm(), r.artHeightCm());
    }

    public Page<ProductResponse> search(Long categoryId, String q, BigDecimal minPrice,
            BigDecimal maxPrice, Boolean inStock, Boolean antique, String sort, int page, int size) {
        Specification<Product> spec = Specification.where(ProductSpecs.categoryIs(categoryId))
                .and(ProductSpecs.nameContains(q))
                .and(ProductSpecs.priceGte(minPrice))
                .and(ProductSpecs.priceLte(maxPrice))
                .and(Boolean.TRUE.equals(inStock) ? ProductSpecs.inStock() : null)
                .and(ProductSpecs.antiqueIs(antique));

        Sort sortObj;
        if ("price_asc".equals(sort)) {
            sortObj = Sort.by("price").ascending();
        } else if ("price_desc".equals(sort)) {
            sortObj = Sort.by("price").descending();
        } else if ("name".equals(sort)) {
            sortObj = Sort.by("name").ascending();
        } else {
            sortObj = Sort.by("id").descending();
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return repo.findAll(spec, pageable).map(this::toResponse);
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

    public ProductOrderInfo getOrderInfo(Long id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + id));
        return new ProductOrderInfo(p.getId(), p.getPrice(), p.getSellerId(), p.getStock(),
                p.getWeightGrams(), p.getLengthCm(), p.getWidthCm(), p.getHeightCm());
    }

    public List<FrameOption> frameOptions(Long id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + id));
        return FramePricing.optionsFor(p);
    }

    public BigDecimal frameCharge(Long productId, String frameType) {
        Product p = repo.findById(productId)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + productId));
        return FramePricing.charge(frameType, p.getArtWidthCm(), p.getArtHeightCm());
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
        p.setAntique(req.antique());
        if (req.antique()) p.setStock(1);
        // Only override when provided (entity defaults cover new products; edits keep existing).
        if (req.weightGrams() != null) p.setWeightGrams(req.weightGrams());
        if (req.lengthCm() != null) p.setLengthCm(req.lengthCm());
        if (req.widthCm() != null) p.setWidthCm(req.widthCm());
        if (req.heightCm() != null) p.setHeightCm(req.heightCm());
        String art = (req.artType() == null || req.artType().isBlank()) ? "NONE" : req.artType().toUpperCase();
        p.setArtType(art);
        if (req.artWidthCm() != null) p.setArtWidthCm(req.artWidthCm());
        if (req.artHeightCm() != null) p.setArtHeightCm(req.artHeightCm());
        List<String> urls = new ArrayList<>();
        if (req.imageUrls() != null) {
            for (String u : req.imageUrls()) urls.add(signer.stripQuery(u));
        }
        p.setImageUrls(urls);
        return p;
    }
}
