package com.modoria.catalog.application.service.category;

import com.modoria.catalog.application.dto.category.CategoryRequestDTO;
import com.modoria.catalog.application.dto.category.CategoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO);

    CategoryResponseDTO getCategoryById(Long id);

    Page<CategoryResponseDTO> getAllCategories(Pageable pageable);

    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO);

    void deleteCategory(Long id);
}
