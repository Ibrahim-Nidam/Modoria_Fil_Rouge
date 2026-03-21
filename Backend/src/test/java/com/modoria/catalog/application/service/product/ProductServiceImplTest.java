package com.modoria.catalog.application.service.product;

import com.modoria.catalog.application.mapper.product.ProductMapper;
import com.modoria.catalog.application.service.season.SeasonService;
import com.modoria.catalog.domain.model.Category;
import com.modoria.catalog.domain.model.Product;
import com.modoria.catalog.domain.repository.CategoryRepository;
import com.modoria.catalog.domain.repository.ProductImageRepository;
import com.modoria.catalog.domain.repository.ProductRepository;
import com.modoria.shared.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private SeasonService seasonService;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void restoreProduct_whenCategoryDeleted_throwsBadRequest() {
        Category category = Category.builder().id(10L).deleted(true).build();
        Product product = Product.builder().id(1L).deleted(true).category(category).build();

        when(productRepository.findWithCategoryAndImagesById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.restoreProduct(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("category is soft-deleted");
    }
}
