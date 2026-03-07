package com.modoria.catalog.application.service.product;

import com.modoria.catalog.application.dto.product.ProductRequestDTO;
import com.modoria.catalog.application.dto.product.ProductResponseDTO;
import com.modoria.catalog.domain.model.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);

    ProductResponseDTO getProductById(Long id);

    Page<ProductResponseDTO> getAllProducts(Pageable pageable);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO);

    void deleteProduct(Long id);

    ProductResponseDTO uploadProductImage(Long productId, MultipartFile file);

    Page<ProductResponseDTO> getProductsBySeason(String season, Pageable pageable);

    Page<ProductResponseDTO> searchProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice, Long categoryId,
            Season season, Pageable pageable);
}
