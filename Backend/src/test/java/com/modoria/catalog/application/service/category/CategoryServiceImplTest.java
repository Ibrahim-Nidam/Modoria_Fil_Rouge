package com.modoria.catalog.application.service.category;

import com.modoria.catalog.application.dto.category.CategoryResponseDTO;
import com.modoria.catalog.application.mapper.category.CategoryMapper;
import com.modoria.catalog.application.service.product.FileStorageService;
import com.modoria.catalog.domain.model.Category;
import com.modoria.catalog.domain.model.Season;
import com.modoria.catalog.domain.repository.CategoryRepository;
import com.modoria.catalog.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CategoryServiceImpl service;

    @Test
    void getCategoryById_includeDeleted_returnsDto() {
        Category category = Category.builder().id(1L).name("Spring").season(Season.SPRING).deleted(true).build();
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(1L);
        dto.setName("Spring");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.countByCategoryId(1L)).thenReturn(2L);
        when(categoryMapper.toResponseDTO(category)).thenReturn(dto);

        CategoryResponseDTO result = service.getCategoryById(1L, true);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductCount()).isEqualTo(2L);
    }
}
