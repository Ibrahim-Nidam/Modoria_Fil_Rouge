package com.modoria.catalog.application.service.product;

import com.modoria.shared.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    @Value("${app.upload.dir:uploads/products}")
    private String uploadDir;

    @Override
    public String storeFile(MultipartFile file, Long productId) {
        validateFile(file);

        String fileName = "product_" + productId + "_" + UUID.randomUUID() + getExtension(file.getOriginalFilename());

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetPath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored product image: {}", fileName);

            return uploadDir + "/" + fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Failed to store image file: " + ex.getMessage(), ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BadRequestException("File size exceeds the 5MB limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Only image files are allowed (JPEG, PNG, GIF, WebP)");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains("."))
            return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
