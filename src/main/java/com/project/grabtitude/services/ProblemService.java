package com.project.grabtitude.services;


import com.project.grabtitude.dto.*;
import com.project.grabtitude.entity.User;
import com.project.grabtitude.helper.CustomPageResponse;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProblemService {
    ProblemResponseDto createProblem(ProblemRequestDto problemRequestDto);

    void deleteProblemById(Long id);

    ProblemResponseDto getById(Long id);

    ProblemResponseDto update(ProblemUpdateDto problemUpdateDto);

    CustomPageResponse<ProblemResponseDto> getProblems(int page, int size);

    CustomPageResponse<ProblemResponseDto> search(String keyword, int page, int size);

    SubmissionResponseDto submit(SubmissionRequestDto submissionRequestDto);

    Map<String, Integer> getDifficultyStats(User user);

    Map<String, Integer> getTopicStats(User user);

    List<ProblemResponseDto> createProblems(List<ProblemRequestDto> problems);

    Object searchByDifficulty(String difficulty, int page, int size);

    Object searchByTopic(Long topicId, int page, int size);

    ProblemResponseDto getNextForProblem(Long id);

    ProblemResponseDto getPreviousForProblem(Long id);

    Integer getTotalProblemsByUser();

    Long getTotalProblems();

    CustomPageResponse<ProblemResponseDto> getByCategory(Long id, int page, int size);

    CustomPageResponse<ProblemResponseDto> filter(FilterDto filterDto, int page, int size);

    void markSolvedAttemptedUnsolved(CustomPageResponse<ProblemResponseDto> customPageResponse, User user);
}
