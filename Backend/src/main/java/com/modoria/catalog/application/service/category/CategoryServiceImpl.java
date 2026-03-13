package com.modoria.catalog.application.service.category;

import com.modoria.catalog.application.dto.category.CategoryRequestDTO;
import com.modoria.catalog.application.dto.category.CategoryResponseDTO;
import com.modoria.catalog.application.mapper.category.CategoryMapper;
import com.modoria.catalog.application.service.product.FileStorageService;
import com.modoria.catalog.domain.model.Category;
import com.modoria.catalog.domain.repository.CategoryRepository;
import com.modoria.catalog.domain.repository.ProductRepository;
import com.modoria.shared.exception.DuplicateResourceException;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
        if (categoryRepository.existsByName(requestDTO.getName())) {
            throw new DuplicateResourceException("Category with name '" + requestDTO.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(requestDTO);
        Category savedCategory = categoryRepository.save(category);
        log.info("Created new category: {}", savedCategory.getName());

        return toResponseDTO(savedCategory, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = findCategoryOrThrow(id);
        return toResponseDTO(category, productRepository.countByCategoryId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponseDTO> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        List<Long> categoryIds = categories.getContent().stream()
            .map(Category::getId)
            .toList();

        Map<Long, Long> productCounts = categoryIds.isEmpty()
            ? Map.of()
            : productRepository.countProductsByCategoryIds(categoryIds).stream()
                .collect(Collectors.toMap(
                    projection -> projection.getCategoryId(),
                    projection -> projection.getProductCount()));

        return categories.map(category -> toResponseDTO(category, productCounts.getOrDefault(category.getId(), 0L)));
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO) {
        Category category = findCategoryOrThrow(id);

        if (categoryRepository.existsByNameAndIdNot(requestDTO.getName(), id)) {
            throw new DuplicateResourceException(
                    "Another category with name '" + requestDTO.getName() + "' already exists");
        }

        categoryMapper.updateEntityFromDto(requestDTO, category);
        Category updatedCategory = categoryRepository.save(category);
        log.info("Updated category with ID: {}", id);

        return toResponseDTO(updatedCategory, productRepository.countByCategoryId(id));
    }

    @Override
    @Transactional
    public CategoryResponseDTO uploadCategoryImage(Long id, MultipartFile file) {
        Category category = findCategoryOrThrow(id);

        fileStorageService.deleteFile(category.getImagePath());
        String imagePath = fileStorageService.storeFile(file, "category", "category_" + id);

        category.setImagePath(imagePath);
        Category savedCategory = categoryRepository.save(category);
        log.info("Updated image for category with ID: {}", id);

        return toResponseDTO(savedCategory, productRepository.countByCategoryId(id));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategoryOrThrow(id);
        fileStorageService.deleteFile(category.getImagePath());
        categoryRepository.delete(category);
        log.info("Deleted category with ID: {}", id);
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    private CategoryResponseDTO toResponseDTO(Category category, long productCount) {
        CategoryResponseDTO responseDTO = categoryMapper.toResponseDTO(category);
        responseDTO.setProductCount(productCount);
        return responseDTO;
    }
}
