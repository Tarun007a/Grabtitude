package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.ProblemRequestDto;
import com.project.grabtitude.entity.Problem;
import com.project.grabtitude.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProblemRequestMapper implements Mapper<Problem, ProblemRequestDto> {
    private final ModelMapper modelMapper;
    public ProblemRequestMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    @Override
    public ProblemRequestDto mapTo(Problem problem) {
        return modelMapper.map(problem, ProblemRequestDto.class);
    }

    @Override
    public Problem mapFrom(ProblemRequestDto problemRequestDto) {
        return modelMapper.map(problemRequestDto, Problem.class);
    }
}
