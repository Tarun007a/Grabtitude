package com.project.grabtitude.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemResponseDto {
    private Long problemId;

    private String title;

    private String description;

    private String difficulty;

    private String topicName;

    private List<ProblemOptionResponseDto> options;

    private LocalDateTime createdAt;

    private String explanation;

    private int acceptance;

    private Long categoryId;

    private String status = "unsolved";
}
