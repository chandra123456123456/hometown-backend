package com.hometown.product.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(schema = "products", name = "product_image_files")
public class ProductImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String contentType;

    private String fileName;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ProductImageFile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
