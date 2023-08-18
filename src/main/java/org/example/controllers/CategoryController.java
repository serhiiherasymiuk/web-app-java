package org.example.controllers;
import org.springframework.web.bind.annotation.*;

import org.example.dao.CategoryDao;
import org.example.dto.category.CategoryItemDTO;
import org.example.utils.HibernateUtil;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.example.entities.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CategoryController {
    SessionFactory sf = HibernateUtil.getSessionFactory();
    CategoryDao categoryDao = new CategoryDao(sf);
    ModelMapper modelMapper = new ModelMapper();

    @GetMapping("/get/{id}")
    public ResponseEntity<CategoryItemDTO> getCategoryById(@PathVariable int id) {
        Category existingCategory = categoryDao.get(id).orElse(null);

        if (existingCategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CategoryItemDTO categoryDTO = modelMapper.map(existingCategory, CategoryItemDTO.class);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<CategoryItemDTO>> index() {
        List<Category> categories = categoryDao.getAll();

        List<CategoryItemDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryItemDTO.class))
                .collect(Collectors.toList());

        return new ResponseEntity<>(categoryDTOs, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<CategoryItemDTO> createCategory(@RequestBody CategoryItemDTO categoryDTO) {
        Category newCategory = modelMapper.map(categoryDTO, Category.class);
        categoryDao.save(newCategory);

        return new ResponseEntity<>(categoryDTO, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<CategoryItemDTO> updateCategory(@RequestBody CategoryItemDTO updatedCategoryDTO) {
        Category existingCategory = categoryDao.get(updatedCategoryDTO.getId()).orElse(null);

        if (existingCategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        modelMapper.map(updatedCategoryDTO, existingCategory);
        categoryDao.update(existingCategory);

        return new ResponseEntity<>(updatedCategoryDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        Category existingCategory = categoryDao.get(id).orElse(null);

        if (existingCategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        categoryDao.delete(existingCategory);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
