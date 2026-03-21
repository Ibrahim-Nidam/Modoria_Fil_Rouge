package com.modoria.catalog.application.service.product;

import com.modoria.catalog.application.dto.product.ProductRequestDTO;
import com.modoria.catalog.application.dto.product.ProductImageResponseDTO;
import com.modoria.catalog.application.dto.product.ProductResponseDTO;
import com.modoria.catalog.domain.model.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);

    ProductResponseDTO getProductById(Long id, boolean includeDeleted);

    Page<ProductResponseDTO> getAllProducts(Pageable pageable, boolean includeDeleted);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO);

    void deleteProduct(Long id);

    ProductResponseDTO restoreProduct(Long id);

    ProductResponseDTO uploadProductImage(Long productId, MultipartFile file);

    List<ProductImageResponseDTO> uploadProductImages(Long productId, List<MultipartFile> files, Integer primaryIndex);

    void deleteProductImage(Long productId, Long imageId);

    ProductResponseDTO setPrimaryProductImage(Long productId, Long imageId);

        Page<ProductResponseDTO> getProductsBySeason(String season, Pageable pageable, boolean includeDeleted);

    Page<ProductResponseDTO> searchProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice, Long categoryId,
            Season season, Pageable pageable, boolean includeDeleted);

    String getCatalogSummary();
}
