package com.hometown.product.image;

import com.hometown.common.web.ApiException;
import com.hometown.product.domain.ProductImageFile;
import com.hometown.product.repo.ProductImageFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class ImageStorageService {

    private final ImageProperties props;
    private final AesImageCipher cipher;
    private final ProductImageFileRepository repo;
    private final SecureRandom rng = new SecureRandom();

    public ImageStorageService(ImageProperties props, AesImageCipher cipher, ProductImageFileRepository repo) {
        this.props = props;
        this.cipher = cipher;
        this.repo = repo;
    }

    public ProductImageFile store(MultipartFile file) throws IOException {
        Path dir = Paths.get(props.getDir());
        Files.createDirectories(dir);

        byte[] randomBytes = new byte[16];
        rng.nextBytes(randomBytes);
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        byte[] encrypted = cipher.encrypt(file.getBytes());
        Files.write(dir.resolve(code + ".enc"), encrypted);

        ProductImageFile entity = new ProductImageFile();
        entity.setCode(code);
        entity.setContentType(file.getContentType());
        entity.setFileName(file.getOriginalFilename());
        return repo.save(entity);
    }

    public byte[] loadDecrypted(String code) throws IOException {
        ProductImageFile entity = repo.findByCode(code)
                .orElseThrow(() -> ApiException.notFound("Image not found: " + code));
        Path file = Paths.get(props.getDir()).resolve(code + ".enc");
        byte[] stored = Files.readAllBytes(file);
        return cipher.decrypt(stored);
    }
}
