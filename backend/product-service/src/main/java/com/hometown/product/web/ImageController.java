package com.hometown.product.web;

import com.hometown.common.web.ApiException;
import com.hometown.product.domain.ProductImageFile;
import com.hometown.product.image.ImageProperties;
import com.hometown.product.image.ImageStorageService;
import com.hometown.product.image.ImageUrlSigner;
import com.hometown.product.image.WatermarkService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
public class ImageController {

    private final ImageStorageService storage;
    private final WatermarkService watermark;
    private final ImageProperties props;
    private final ImageUrlSigner signer;

    public ImageController(ImageStorageService storage, WatermarkService watermark,
                           ImageProperties props, ImageUrlSigner signer) {
        this.storage = storage;
        this.watermark = watermark;
        this.props = props;
        this.signer = signer;
    }

    @PostMapping(value = "/api/images", consumes = MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        ProductImageFile entity = storage.store(file);
        String base = "/api/images/" + entity.getCode();
        return Map.of(
                "code", entity.getCode(),
                "url", base,
                "previewUrl", signer.sign(base),
                "contentType", entity.getContentType() != null ? entity.getContentType() : "image/png"
        );
    }

    @GetMapping("/api/images/{code}")
    public ResponseEntity<byte[]> get(@PathVariable String code,
                                      @RequestParam(required = false) Long exp,
                                      @RequestParam(required = false) String sig) throws IOException {
        if (!signer.verify(code, exp, sig)) {
            throw ApiException.forbidden("Image link is invalid or has expired");
        }
        byte[] bytes = watermark.apply(storage.loadDecrypted(code), props.getWatermarkText());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.noStore())
                .body(bytes);
    }
}
