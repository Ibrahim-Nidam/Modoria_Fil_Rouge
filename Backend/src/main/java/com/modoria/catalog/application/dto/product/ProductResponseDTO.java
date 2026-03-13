package com.modoria.catalog.application.dto.product;

import com.modoria.catalog.application.dto.category.CategoryResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String season;
    private CategoryResponseDTO category;
    private String primaryImagePath;
    private List<ProductImageResponseDTO> images;
}
