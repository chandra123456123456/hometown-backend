package com.hometown.product.web;

import com.hometown.product.domain.ProductImageFile;
import com.hometown.product.image.ImageProperties;
import com.hometown.product.image.ImageStorageService;
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

    public ImageController(ImageStorageService storage, WatermarkService watermark, ImageProperties props) {
        this.storage = storage;
        this.watermark = watermark;
        this.props = props;
    }

    @PostMapping(value = "/api/images", consumes = MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        ProductImageFile entity = storage.store(file);
        return Map.of(
                "code", entity.getCode(),
                "url", "/api/images/" + entity.getCode(),
                "contentType", entity.getContentType() != null ? entity.getContentType() : "image/png"
        );
    }

    @GetMapping("/api/images/{code}")
    public ResponseEntity<byte[]> get(@PathVariable String code) throws IOException {
        byte[] bytes = watermark.apply(storage.loadDecrypted(code), props.getWatermarkText());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.noStore())
                .body(bytes);
    }
}
