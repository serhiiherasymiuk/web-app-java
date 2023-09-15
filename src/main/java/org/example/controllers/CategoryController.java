package org.example.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.example.dto.category.CategoryCreateDTO;
import org.example.dto.category.CategoryItemDTO;
import org.example.dto.category.CategoryUpdateDTO;
import org.example.entities.CategoryEntity;
import org.example.entities.ProductEntity;
import org.example.entities.ProductImageEntity;
import org.example.mappers.CategoryMapper;
import org.example.repositories.CategoryRepository;
import org.example.storage.FileSaveFormat;
import org.example.storage.StorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("api/categories")
@SecurityRequirement(name = "my-api")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final StorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryEntity> create(@ModelAttribute CategoryCreateDTO dto) {
        CategoryEntity cat = categoryMapper.categoryByCreateCategoryDTO(dto);

        String fileName = storageService.saveThumbnailator(dto.getImage(), FileSaveFormat.WEBP);

        cat.setImage(fileName);
        categoryRepository.save(cat);

        return ResponseEntity.ok().body(cat);
    }

    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryEntity> updateCategory(@PathVariable int id, @ModelAttribute CategoryUpdateDTO dto) {
        Optional<CategoryEntity> existingCategory = categoryRepository.findById(id);

        if (existingCategory.isPresent()) {
            CategoryEntity cat = categoryMapper.categoryByUpdateCategoryDTO(dto);
            cat.setId(id);

            storageService.removeFile(existingCategory.get().getImage());

            String fileName = storageService.saveThumbnailator(dto.getImage(), FileSaveFormat.WEBP);
            cat.setImage(fileName);

            categoryRepository.save(cat);
            return ResponseEntity.ok(cat);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoryItemDTO> getCategoryById(@PathVariable int id) {
        return categoryRepository.findById(id)
                .map(category -> ResponseEntity.ok().body(categoryMapper.categoryToItemDTO(category)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<CategoryItemDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryMapper.listCategoriesToItemDTO(categoryRepository.findAll()));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        Optional<CategoryEntity> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            storageService.removeFile(optionalCategory.get().getImage());
            for (ProductEntity product : optionalCategory.get().getProducts()) {
                for (ProductImageEntity image : product.getImages()) {
                    storageService.removeFile(image.getImage());
                }
            }
            categoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
