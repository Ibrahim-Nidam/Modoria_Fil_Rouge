package com.modoria.catalog.application.dto.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String season;
    private String description;
    private String imagePath;
    private Boolean deleted;
    private Long productCount;
}
