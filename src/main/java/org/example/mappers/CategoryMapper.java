package org.example.mappers;

import org.example.dto.category.CategoryCreateDTO;
import org.example.dto.category.CategoryItemDTO;
import org.example.dto.category.CategoryUpdateDTO;
import org.example.entities.CategoryEntity;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryItemDTO categoryToItemDTO(CategoryEntity category);
    List<CategoryItemDTO> listCategoriesToItemDTO(List<CategoryEntity> list);
    CategoryEntity CategoryByCreateCategoryDTO(CategoryCreateDTO dto);
    CategoryEntity CategoryByUpdateCategoryDTO(CategoryUpdateDTO dto);
}