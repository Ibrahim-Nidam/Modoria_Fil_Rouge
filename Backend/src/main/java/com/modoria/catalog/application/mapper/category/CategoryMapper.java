package com.modoria.catalog.application.mapper.category;

import com.modoria.catalog.application.dto.category.CategoryRequestDTO;
import com.modoria.catalog.application.dto.category.CategoryResponseDTO;
import com.modoria.catalog.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequestDTO dto);

    CategoryResponseDTO toResponseDTO(Category entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CategoryRequestDTO dto, @MappingTarget Category entity);
}
