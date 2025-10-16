package com.project.grabtitude.services;

import com.project.grabtitude.dto.CategoryRequestDto;
import com.project.grabtitude.dto.CategoryResponseDto;
import com.project.grabtitude.helper.CustomPageResponse;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto getCategory(Long id);

    CustomPageResponse<CategoryResponseDto> getCategories(int page, int size);

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto);

    String deleteCategory(Long id);

    Long getTotalCategories();
}
