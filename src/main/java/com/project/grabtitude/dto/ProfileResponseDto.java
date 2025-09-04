package com.project.grabtitude.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileResponseDto {
    String userId;
    String name;
    String about;
    Double accuracy;
    String institute;
    String country;
    String linkedIn;
    String github;
    Integer questionsSolved;
    Map<String, Integer> difficultyLevelWiseQuestionsSolved;
    Map<String, Integer> topicWiseQuestionsSolved;
}
