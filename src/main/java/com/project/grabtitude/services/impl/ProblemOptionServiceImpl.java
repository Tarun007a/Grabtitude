package com.project.grabtitude.services.impl;

import com.project.grabtitude.dto.ProblemOptionResponseDto;
import com.project.grabtitude.entity.Problem;
import com.project.grabtitude.entity.ProblemOption;
import com.project.grabtitude.mapper.Mapper;
import com.project.grabtitude.repository.ProblemOptionRepo;
import com.project.grabtitude.services.ProblemOptionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProblemOptionServiceImpl implements ProblemOptionService {
    private final ProblemOptionRepo problemOptionRepo;
    private final Mapper<ProblemOption, ProblemOptionResponseDto> problemOptionResponseMapper;
    public ProblemOptionServiceImpl(ProblemOptionRepo problemOptionRepo, Mapper<ProblemOption, ProblemOptionResponseDto> problemOptionResponseMapper){
        this.problemOptionRepo = problemOptionRepo;
        this.problemOptionResponseMapper = problemOptionResponseMapper;
    }
    @Override
    public List<ProblemOptionResponseDto> getOptionForProblem(Problem problem) {
        List<ProblemOption> options = problemOptionRepo.findAllByProblem(problem);
        List<ProblemOptionResponseDto> optionDtos = new ArrayList<>();

        for(ProblemOption option : options){
            optionDtos.add(problemOptionResponseMapper.mapTo(option));
        }
        return optionDtos;
    }

    @Override
    public List<ProblemOption> getOptionsForDelete(Problem problem) {
        return problemOptionRepo.findAllByProblem(problem);
    }
}
