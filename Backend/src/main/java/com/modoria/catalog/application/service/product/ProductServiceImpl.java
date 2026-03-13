package com.modoria.catalog.application.service.product;

import com.modoria.catalog.application.dto.product.ProductRequestDTO;
import com.modoria.catalog.application.dto.product.ProductResponseDTO;
import com.modoria.catalog.application.mapper.product.ProductMapper;
import com.modoria.catalog.application.service.season.SeasonService;
import com.modoria.catalog.domain.model.Category;
import com.modoria.catalog.domain.model.Product;
import com.modoria.catalog.domain.model.Season;
import com.modoria.catalog.domain.repository.CategoryRepository;
import com.modoria.catalog.domain.repository.ProductRepository;
import com.modoria.catalog.domain.specification.ProductSpecification;
import com.modoria.shared.exception.BadRequestException;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;
    private final SeasonService seasonService;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        Category category = getCategoryOrThrow(requestDTO.getCategoryId());

        Product product = productMapper.toEntity(requestDTO);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        log.info("Created new product: {}", savedProduct.getName());

        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = getProductOrThrow(id);
        return productMapper.toResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        Product product = getProductOrThrow(id);
        Category category = getCategoryOrThrow(requestDTO.getCategoryId());

        productMapper.updateEntityFromDto(requestDTO, product);
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        log.info("Updated product with ID: {}", id);

        return productMapper.toResponseDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductOrThrow(id);
        productRepository.delete(product);
        log.info("Deleted product with ID: {}", id);
    }

    @Override
    @Transactional
    public ProductResponseDTO uploadProductImage(Long productId, MultipartFile file) {
        Product product = getProductOrThrow(productId);
        String imagePath = fileStorageService.storeFile(file, productId);
        product.setImagePath(imagePath);
        Product updatedProduct = productRepository.save(product);
        log.info("Uploaded image for product ID: {}", productId);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getProductsBySeason(String seasonStr, Pageable pageable) {
        Season season;
        if ("current".equalsIgnoreCase(seasonStr)) {
            season = seasonService.getCurrentSeason();
        } else {
            try {
                season = Season.valueOf(seasonStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid season: " + seasonStr);
            }
        }

        return productRepository.findBySeason(season, pageable)
                .map(productMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice,
            Long categoryId, Season season, Pageable pageable) {
        Specification<Product> spec = Specification.allOf(
                ProductSpecification.withKeyword(keyword),
                ProductSpecification.withPriceRange(minPrice, maxPrice),
                ProductSpecification.withCategoryId(categoryId),
                ProductSpecification.withSeason(season));

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponseDTO);
    }

    private Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public String getCatalogSummary() {
        return productRepository.findAll().stream()
                .map(p -> String.format("ID: %d, Name: %s, Category: %s, Price: %.2f, Description: %s",
                        p.getId(), p.getName(), p.getCategory().getName(), p.getPrice(), p.getDescription()))
                .collect(java.util.stream.Collectors.joining("\n"));
    }
}
