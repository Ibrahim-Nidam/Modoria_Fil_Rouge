package com.modoria.catalog.application.service.product;

import com.modoria.shared.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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

    @Value("${app.upload.dir:storage}")
    private String uploadRootDir;

    @Override
    public String storeFile(MultipartFile file, String relativeFolder, String filePrefix) {
        validateFile(file);

        String safeFolder = sanitizeFolderPath(relativeFolder, "uploads");
        String safePrefix = sanitizeFilePrefix(filePrefix, "file");
        String fileName = safePrefix + "_" + UUID.randomUUID() + getExtension(file.getOriginalFilename());

        try {
            Path uploadPath = Paths.get(uploadRootDir).resolve(safeFolder).normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetPath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored image: {} in folder {}", fileName, safeFolder);

            return "/storage/" + safeFolder + "/" + fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Failed to store image file: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deleteFile(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return;
        }

        String normalizedPath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
        if (!normalizedPath.startsWith("storage/")) {
            log.warn("Skipping file deletion for unexpected path: {}", imagePath);
            return;
        }

        String relativePath = normalizedPath.substring("storage/".length());

        try {
            Path filePath = Paths.get(uploadRootDir, relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException | InvalidPathException ex) {
            log.warn("Failed to delete image file {}: {}", imagePath, ex.getMessage());
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

    private String sanitizeFolderPath(String folderPath, String fallback) {
        String normalizedPath = folderPath == null ? "" : folderPath.trim().toLowerCase();
        if (normalizedPath.isBlank()) {
            return fallback;
        }

        String[] rawSegments = normalizedPath.split("[\\\\/]+");
        StringBuilder builder = new StringBuilder();
        for (String segment : rawSegments) {
            String sanitizedSegment = sanitizeSegment(segment);
            if (sanitizedSegment.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append('/');
            }
            builder.append(sanitizedSegment);
        }

        return builder.isEmpty() ? fallback : builder.toString();
    }

    private String sanitizeFilePrefix(String filePrefix, String fallback) {
        String sanitized = sanitizeSegment(filePrefix == null ? "" : filePrefix);
        return sanitized.isBlank() ? fallback : sanitized;
    }

    private String sanitizeSegment(String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase();
        normalized = normalized.replaceAll("[^a-z0-9-_]", "-");
        normalized = normalized.replaceAll("-+", "-");
        normalized = normalized.replaceAll("^-|-$", "");
        return normalized;
    }
}
