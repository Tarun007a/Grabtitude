package com.project.grabtitude.dto;

import jakarta.persistence.Access;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionResponseDto {
    private Long submissionId;
    private boolean isCorrect;
    private LocalDateTime submittedAt;
    private Long problemId;
}
