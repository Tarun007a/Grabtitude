package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.ProblemOptionRequestDto;
import com.project.grabtitude.entity.ProblemOption;
import com.project.grabtitude.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProblemOptionRequestMapper implements Mapper<ProblemOption, ProblemOptionRequestDto> {
    private final ModelMapper modelMapper;
    public ProblemOptionRequestMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    @Override
    public ProblemOption mapFrom(ProblemOptionRequestDto problemOptionRequestDto) {
        return modelMapper.map(problemOptionRequestDto, ProblemOption.class);
    }

    @Override
    public ProblemOptionRequestDto mapTo(ProblemOption problemOption) {
        return modelMapper.map(problemOption, ProblemOptionRequestDto.class);
    }
}
