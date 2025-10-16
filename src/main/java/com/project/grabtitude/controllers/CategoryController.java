package com.project.grabtitude.controllers;

import com.project.grabtitude.dto.CategoryRequestDto;
import com.project.grabtitude.dto.CategoryResponseDto;
import com.project.grabtitude.helper.AppConstants;
import com.project.grabtitude.helper.CustomPageResponse;
import com.project.grabtitude.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/category")
public class CategoryController {
    private CategoryService categoryService;
    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto){
        System.out.println("Called create topic");
        return new ResponseEntity<>(categoryService.createCategory(categoryRequestDto), HttpStatus.CREATED);
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id){
        System.out.println("Called id " + id);
        return new ResponseEntity<>(categoryService.getCategory(id), HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<CustomPageResponse<CategoryResponseDto>> getAllCategories(
            @RequestParam(value = "page", defaultValue = AppConstants.page) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.size) int size
    ){
        return new ResponseEntity<>(categoryService.getCategories(page, size), HttpStatus.OK);
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long id,
                                                              @RequestBody CategoryRequestDto categoryRequestDto){
        return new ResponseEntity<>(categoryService.updateCategory(id, categoryRequestDto), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        return new ResponseEntity<>(categoryService.deleteCategory(id), HttpStatus.ACCEPTED);
    }
}
