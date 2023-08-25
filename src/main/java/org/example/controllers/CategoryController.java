package org.example.controllers;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.dto.category.CategoryCreateDTO;
import org.example.dto.category.CategoryItemDTO;
import org.example.dto.category.CategoryUpdateDTO;
import org.example.entities.CategoryEntity;
import org.example.mappers.CategoryMapper;
import org.example.repositories.CategoryRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("api/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryEntity> create(@ModelAttribute CategoryCreateDTO dto) {
        CategoryEntity cat = categoryMapper.CategoryByCreateCategoryDTO(dto);

        if (dto.getImageFile() != null) {
            String rootDirectory = System.getProperty("user.dir");
            String imageDirectoryPath = rootDirectory + "/images/";

            try {
                File imageDirectory = new File(imageDirectoryPath);
                if (!imageDirectory.exists()) {
                    imageDirectory.mkdirs();
                }
                String originalFilename = dto.getImageFile().getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String imageName = String.format("%s%s", RandomStringUtils.randomAlphanumeric(16), fileExtension);
                File file = new File(imageDirectory, imageName);
                dto.getImageFile().transferTo(file);
                cat.setImage(imageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        categoryRepository.save(cat);

        return ResponseEntity.ok().body(cat);
    }

    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryEntity> updateCategory(@PathVariable int id, @ModelAttribute CategoryUpdateDTO dto) {
        Optional<CategoryEntity> existingCategory = categoryRepository.findById(id);

        if (existingCategory.isPresent()) {
            CategoryEntity cat = categoryMapper.CategoryByUpdateCategoryDTO(dto);
            cat.setId(id);

            String oldImageName = existingCategory.get().getImage();
            if (oldImageName != null) {
                String rootDirectory = System.getProperty("user.dir");
                String imageDirectoryPath = rootDirectory + "/images/";

                File oldImageFile = new File(imageDirectoryPath, oldImageName);
                if (oldImageFile.exists()) {
                    oldImageFile.delete();
                }
            }

            if (dto.getImageFile() != null) {
                String rootDirectory = System.getProperty("user.dir");
                String imageDirectoryPath = rootDirectory + "/images/";

                try {
                    File imageDirectory = new File(imageDirectoryPath);
                    if (!imageDirectory.exists()) {
                        imageDirectory.mkdirs();
                    }
                    String originalFilename = dto.getImageFile().getOriginalFilename();
                    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String imageName = String.format("%s%s", RandomStringUtils.randomAlphanumeric(16), fileExtension);
                    File file = new File(imageDirectory, imageName);
                    dto.getImageFile().transferTo(file);
                    cat.setImage(imageName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            categoryRepository.save(cat);
            return ResponseEntity.ok(cat);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("{id}")
    public ResponseEntity<CategoryEntity> getCategory(@PathVariable int id) {
        return categoryRepository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<CategoryItemDTO>> getAllCategories() {
        List<CategoryItemDTO> items = categoryMapper.listCategoriesToItemDTO(categoryRepository.findAll());
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        Optional<CategoryEntity> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            String oldImageName = optionalCategory.get().getImage();
            if (oldImageName != null) {
                String rootDirectory = System.getProperty("user.dir");
                String imageDirectoryPath = rootDirectory + "/images/";

                File oldImageFile = new File(imageDirectoryPath, oldImageName);
                if (oldImageFile.exists()) {
                    oldImageFile.delete();
                }
            }
            categoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
