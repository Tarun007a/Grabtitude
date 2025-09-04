package com.project.grabtitude.services;

import com.project.grabtitude.dto.ProblemOptionResponseDto;
import com.project.grabtitude.entity.Problem;
import com.project.grabtitude.entity.ProblemOption;

import java.util.List;

public interface ProblemOptionService {
    List<ProblemOptionResponseDto> getOptionForProblem(Problem problem);

    List<ProblemOption> getOptionsForDelete(Problem problem);
}
