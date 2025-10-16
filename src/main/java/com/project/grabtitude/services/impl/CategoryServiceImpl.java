package com.project.grabtitude.services.impl;

import com.project.grabtitude.dto.CategoryRequestDto;
import com.project.grabtitude.dto.CategoryResponseDto;
import com.project.grabtitude.entity.Category;
import com.project.grabtitude.helper.CustomPageResponse;
import com.project.grabtitude.helper.ResourceNotFoundException;
import com.project.grabtitude.mapper.impl.CategoryRequestMapper;
import com.project.grabtitude.mapper.impl.CategoryResponseMapper;
import com.project.grabtitude.repository.CategoryRepo;
import com.project.grabtitude.services.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepo categoryRepo;
    private CategoryResponseMapper categoryResponseMapper;
    private CategoryRequestMapper categoryRequestMapper;
    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        Category category = categoryRequestMapper.mapFrom(categoryRequestDto);
        Category savedCategory = categoryRepo.save(category);
        return categoryResponseMapper.mapTo(savedCategory);
    }

    @Override
    public CategoryResponseDto getCategory(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
        System.out.println(category);
        return categoryResponseMapper.mapTo(category);
    }

    @Override
    public CustomPageResponse<CategoryResponseDto> getCategories(int page, int size) {
        Sort sort = Sort.by("id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Category> categoriesPage = categoryRepo.findAll(pageable);

        CustomPageResponse<CategoryResponseDto> responsePage = new CustomPageResponse<>();
        Page<CategoryResponseDto> categoryResponseDtoPage = categoriesPage.map(category -> {
            return categoryResponseMapper.mapTo(category);
        });

        responsePage.setContent(categoryResponseDtoPage.getContent());
        responsePage.setPageNumber(categoryResponseDtoPage.getNumber());
        responsePage.setPageSize(categoryResponseDtoPage.getSize());
        responsePage.setLast(categoryResponseDtoPage.isLast());
        responsePage.setFirst(categoryResponseDtoPage.isFirst());
        responsePage.setTotalPages(categoryResponseDtoPage.getTotalPages());
        responsePage.setTotalNumberOfElements(categoryResponseDtoPage.getTotalElements());
        responsePage.setNumberOfElements(categoryResponseDtoPage.getNumberOfElements());

        return responsePage;
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id : " + id));
        category.setName(categoryRequestDto.getName());
        category.setDescription(categoryRequestDto.getDescription());
        Category savedCategory = categoryRepo.save(category);
        return categoryResponseMapper.mapTo(savedCategory);
    }

    @Override
    public String deleteCategory(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id : " + id));
        categoryRepo.delete(category);
        return "Deleted successfully";
    }

    @Override
    public Long getTotalCategories() {
        return categoryRepo.count();
    }
}