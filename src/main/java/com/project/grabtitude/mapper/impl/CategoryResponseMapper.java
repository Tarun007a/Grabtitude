package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.CategoryResponseDto;
import com.project.grabtitude.entity.Category;
import com.project.grabtitude.mapper.Mapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CategoryResponseMapper implements Mapper<Category, CategoryResponseDto> {
    private ModelMapper modelMapper;
    @Override
    public CategoryResponseDto mapTo(Category category) {
        return modelMapper.map(category, CategoryResponseDto.class);
    }

    @Override
    public Category mapFrom(CategoryResponseDto categoryResponseDto) {
        return modelMapper.map(categoryResponseDto, Category.class);
    }
}
