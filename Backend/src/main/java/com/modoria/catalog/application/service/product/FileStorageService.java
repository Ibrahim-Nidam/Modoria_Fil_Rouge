package com.modoria.catalog.application.service.product;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String relativeFolder, String filePrefix);

    void deleteFile(String imagePath);
}
