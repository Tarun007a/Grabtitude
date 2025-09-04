package com.project.grabtitude.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionRequestDto {
    @NotNull(message = "Please enter problem id for submission")
    private Long problemId;

    @NotNull(message = "Please enter option id for submission")
    private Long optionId;
}
