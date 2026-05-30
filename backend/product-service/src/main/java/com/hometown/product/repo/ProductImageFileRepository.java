package com.hometown.product.repo;

import com.hometown.product.domain.ProductImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductImageFileRepository extends JpaRepository<ProductImageFile, Long> {

    Optional<ProductImageFile> findByCode(String code);
}
