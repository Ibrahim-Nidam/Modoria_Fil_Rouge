package com.modoria.catalog.application.service.category;

import com.modoria.catalog.application.dto.category.CategoryRequestDTO;
import com.modoria.catalog.application.dto.category.CategoryResponseDTO;
import com.modoria.catalog.domain.model.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface CategoryService {
    CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO);

    CategoryResponseDTO getCategoryById(Long id);

    Page<CategoryResponseDTO> getAllCategories(Pageable pageable, Season season);

    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO);

    CategoryResponseDTO uploadCategoryImage(Long id, MultipartFile file);

    void deleteCategory(Long id);
}
