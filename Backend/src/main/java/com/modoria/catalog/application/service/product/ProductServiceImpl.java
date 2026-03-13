package com.modoria.catalog.application.service.product;

import com.modoria.catalog.application.dto.product.ProductRequestDTO;
import com.modoria.catalog.application.dto.product.ProductImageResponseDTO;
import com.modoria.catalog.application.dto.product.ProductResponseDTO;
import com.modoria.catalog.application.mapper.product.ProductMapper;
import com.modoria.catalog.application.service.season.SeasonService;
import com.modoria.catalog.domain.model.Category;
import com.modoria.catalog.domain.model.Product;
import com.modoria.catalog.domain.model.ProductImage;
import com.modoria.catalog.domain.model.Season;
import com.modoria.catalog.domain.repository.CategoryRepository;
import com.modoria.catalog.domain.repository.ProductImageRepository;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
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
        ensureImageFolder(savedProduct);
        savedProduct = productRepository.save(savedProduct);
        log.info("Created new product: {}", savedProduct.getName());

        return mapToProductResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = getProductOrThrow(id);
        return mapToProductResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToProductResponse);
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

        return mapToProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductOrThrow(id);

        for (ProductImage image : productImageRepository.findByProductIdOrderByIdAsc(product.getId())) {
            fileStorageService.deleteFile(image.getImagePath());
        }

        fileStorageService.deleteFile(product.getImagePath());
        productRepository.delete(product);
        log.info("Deleted product with ID: {}", id);
    }

    @Override
    @Transactional
    public ProductResponseDTO uploadProductImage(Long productId, MultipartFile file) {
        uploadProductImages(productId, List.of(file), null);
        return getProductById(productId);
    }

    @Override
    @Transactional
    public List<ProductImageResponseDTO> uploadProductImages(Long productId, List<MultipartFile> files, Integer primaryIndex) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("At least one image file is required");
        }

        Product product = getProductOrThrow(productId);
        ensureImageFolder(product);

        List<MultipartFile> validFiles = files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();

        if (validFiles.isEmpty()) {
            throw new BadRequestException("At least one non-empty image file is required");
        }

        List<ProductImage> createdImages = new ArrayList<>();
        for (MultipartFile file : validFiles) {
            String imagePath = fileStorageService.storeFile(
                file,
                buildProductStorageFolder(product),
                "product_" + product.getId());
            ProductImage image = ProductImage.builder()
                    .imagePath(imagePath)
                    .primary(false)
                    .product(product)
                    .build();
            createdImages.add(image);
        }

        productImageRepository.saveAll(createdImages);

        List<ProductImage> allImages = productImageRepository.findByProductIdOrderByIdAsc(productId);
        boolean hasExistingPrimary = allImages.stream().anyMatch(ProductImage::isPrimary);

        if (primaryIndex != null) {
            if (primaryIndex < 0 || primaryIndex >= createdImages.size()) {
                throw new BadRequestException("primaryIndex is out of range for uploaded images");
            }
            ProductImage target = createdImages.get(primaryIndex);
            applyPrimaryImage(product, target.getId());
        } else if (!hasExistingPrimary) {
            applyPrimaryImage(product, createdImages.get(0).getId());
        } else {
            syncLegacyPrimaryPath(product);
        }

        log.info("Uploaded {} images for product ID: {}", createdImages.size(), productId);
        return createdImages.stream()
                .map(this::toImageResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteProductImage(Long productId, Long imageId) {
        Product product = getProductOrThrow(productId);
        ProductImage image = productImageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + imageId));

        boolean wasPrimary = image.isPrimary();
        fileStorageService.deleteFile(image.getImagePath());
        productImageRepository.delete(image);

        if (wasPrimary) {
            List<ProductImage> remaining = productImageRepository.findByProductIdOrderByIdAsc(productId);
            if (!remaining.isEmpty()) {
                applyPrimaryImage(product, remaining.get(0).getId());
            } else {
                product.setImagePath(null);
                productRepository.save(product);
            }
        }

        log.info("Deleted image {} for product ID: {}", imageId, productId);
    }

    @Override
    @Transactional
    public ProductResponseDTO setPrimaryProductImage(Long productId, Long imageId) {
        Product product = getProductOrThrow(productId);
        productImageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + imageId));

        applyPrimaryImage(product, imageId);
        log.info("Set image {} as primary for product ID: {}", imageId, productId);
        return mapToProductResponse(product);
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
            .map(this::mapToProductResponse);
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
            .map(this::mapToProductResponse);
    }

    private Product getProductOrThrow(Long id) {
        return productRepository.findWithCategoryAndImagesById(id)
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

    private ProductResponseDTO mapToProductResponse(Product product) {
        ProductResponseDTO dto = productMapper.toResponseDTO(product);
        List<ProductImageResponseDTO> images = productImageRepository.findByProductIdOrderByIdAsc(product.getId())
                .stream()
                .map(this::toImageResponse)
                .toList();

        dto.setImages(images);
        dto.setPrimaryImagePath(images.stream()
                .filter(ProductImageResponseDTO::isPrimary)
                .map(ProductImageResponseDTO::getImagePath)
                .findFirst()
                .orElse(product.getImagePath()));
        return dto;
    }

    private ProductImageResponseDTO toImageResponse(ProductImage image) {
        return new ProductImageResponseDTO(image.getId(), image.getImagePath(), image.isPrimary());
    }

    private void ensureImageFolder(Product product) {
        if (product.getImageFolder() != null && !product.getImageFolder().isBlank()) {
            return;
        }

        String baseFolder = slugify(product.getName());
        String candidate = baseFolder;
        int attempt = 1;

        while (productRepository.existsByImageFolder(candidate)) {
            candidate = baseFolder + "-" + product.getId() + "-" + attempt;
            attempt++;
        }

        product.setImageFolder(candidate);
    }

    private String slugify(String value) {
        String normalized = value == null ? "product" : value.trim().toLowerCase(Locale.ROOT);
        normalized = normalized.replaceAll("[^a-z0-9]+", "-");
        normalized = normalized.replaceAll("^-|-$", "");
        return normalized.isBlank() ? "product" : normalized;
    }

    private void applyPrimaryImage(Product product, Long primaryImageId) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderByIdAsc(product.getId());
        ProductImage primaryImage = images.stream()
                .filter(img -> img.getId().equals(primaryImageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + primaryImageId));

        for (ProductImage image : images) {
            image.setPrimary(image.getId().equals(primaryImageId));
        }

        productImageRepository.saveAll(images);
        product.setImagePath(primaryImage.getImagePath());
        productRepository.save(product);
    }

    private void syncLegacyPrimaryPath(Product product) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderByIdAsc(product.getId());
        product.setImagePath(images.stream()
                .filter(ProductImage::isPrimary)
                .max(Comparator.comparing(ProductImage::getId))
                .map(ProductImage::getImagePath)
                .orElse(null));
        productRepository.save(product);
    }

    private String buildProductStorageFolder(Product product) {
        String seasonFolder = product.getSeason() == null
                ? "unassigned"
                : slugify(product.getSeason().name());
        return "products/" + seasonFolder + "/" + product.getImageFolder();
    }
}
