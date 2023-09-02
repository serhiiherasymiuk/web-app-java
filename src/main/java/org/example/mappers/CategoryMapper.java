package org.example.mappers;

import org.example.dto.category.CategoryCreateDTO;
import org.example.dto.category.CategoryItemDTO;
import org.example.dto.category.CategoryUpdateDTO;
import org.example.entities.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface CategoryMapper {
    CategoryItemDTO categoryToItemDTO(CategoryEntity category);
    List<CategoryItemDTO> listCategoriesToItemDTO(List<CategoryEntity> list);
    @Mapping(target = "image", ignore = true)
    CategoryEntity categoryByCreateCategoryDTO(CategoryCreateDTO dto);
    @Mapping(target = "image", ignore = true)
    CategoryEntity categoryByUpdateCategoryDTO(CategoryUpdateDTO dto);
}