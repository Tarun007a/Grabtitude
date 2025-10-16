package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.ProblemResponseDto;
import com.project.grabtitude.entity.Problem;
import com.project.grabtitude.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProblemResponseMapper implements Mapper<Problem, ProblemResponseDto> {
    private final ModelMapper modelMapper;

    public ProblemResponseMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public ProblemResponseDto mapTo(Problem problem) {
        return modelMapper.map(problem, ProblemResponseDto.class);
    }

    @Override
    public Problem mapFrom(ProblemResponseDto problemResponseDto) {
        return modelMapper.map(problemResponseDto, Problem.class);
    }
}
