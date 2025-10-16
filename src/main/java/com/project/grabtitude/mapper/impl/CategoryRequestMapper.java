package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.CategoryRequestDto;
import com.project.grabtitude.entity.Category;
import com.project.grabtitude.mapper.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CategoryRequestMapper implements Mapper<Category, CategoryRequestDto> {
    private ModelMapper modelMapper;
    @Override
    public CategoryRequestDto mapTo(Category category) {
        return modelMapper.map(category, CategoryRequestDto.class);
    }

    @Override
    public Category mapFrom(CategoryRequestDto categoryRequestDto) {
        return modelMapper.map(categoryRequestDto, Category.class);
    }
}
