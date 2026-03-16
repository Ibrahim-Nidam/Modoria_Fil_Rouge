package com.modoria.catalog.application.dto.category;

import com.modoria.catalog.domain.model.Season;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    private String name;

    @NotNull(message = "Category season is required")
    private Season season;

    private String description;
}
