package com.project.grabtitude.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {
    @NotNull(message = "Difficulty for filter cannot be null")
    @Size(min = 2, max = 10, message = "Difficulty should be between 2 to 10 characters")
    @Pattern(
            regexp = "^(EASY|MEDIUM|HARD|EXPERT|ANY)$",
            message = "Difficulty must be one of: EASY, MEDIUM, HARD, EXPERT, ANY"
    )
    String Difficulty;

    @NotNull(message = "Status for filter cannot be null")
    @Size(min = 2, max = 10, message = "Status should be between 2 to 10 characters")
    @Pattern(
            regexp = "^(SOLVED|UNSOLVED|ATTEMPTED|ANY)$",
            message = "Status must be one of: SOLVED, UNSOLVED, ATTEMPTED, ANY"
    )
    String status;

    @NotNull(message = "Topic id for filter cannot be null")
    Long topicId;
}
