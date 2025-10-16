package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.ProblemOptionResponseDto;
import com.project.grabtitude.entity.ProblemOption;
import com.project.grabtitude.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProblemOptionResponseMapper implements Mapper<ProblemOption, ProblemOptionResponseDto> {
    private final ModelMapper modelMapper;
    public ProblemOptionResponseMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public ProblemOptionResponseDto mapTo(ProblemOption problemOption) {
        return modelMapper.map(problemOption, ProblemOptionResponseDto.class);
    }

    @Override
    public ProblemOption mapFrom(ProblemOptionResponseDto problemOptionResponseDto) {
        return modelMapper.map(problemOptionResponseDto, ProblemOption.class);
    }
}
