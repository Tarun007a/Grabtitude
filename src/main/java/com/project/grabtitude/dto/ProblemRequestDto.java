package com.project.grabtitude.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProblemRequestDto {
    @NotNull(message = "Please enter title of topic")
    @Size(min = 1, max = 50, message = "Problem title size must be between 1 to 50 characters")
    private String title;

    @NotNull(message = "Please enter description for problem")
    @Size(min = 10, message = "Problem must be descriptive should contain at least 10 characters")
    private String description;

    @Pattern(
            regexp = "^(EASY|MEDIUM|HARD|EXPERT)$",
            message = "Difficulty must be one of: EASY, MEDIUM, HARD, EXPERT"
    )
    private String difficulty;

    @NotNull(message = "Please enter topic Id of problem")
    private long topicId;

    @Valid
    @Size(min = 2, max = 10, message = "Please enter problem options, minimum 2 and maximum 10")
    private List<ProblemOptionRequestDto> options;
}
