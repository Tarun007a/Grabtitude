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
public class ProblemUpdateDto {
    @NotNull(message = "Please enter problem id to be updated, id is returned when creating a problem")
    private Long problemId;

    @NotNull(message = "Please enter title for problem")
    @Size(min = 1, max = 100, message = "Problem title size must be between 1 to 100 characters")
    private String title;

    @NotNull(message = "Please enter description for problem")
    @Size(min = 10, message = "Problem must be descriptive should contain at least 10 characters")
    private String description;

    @Pattern(
            regexp = "^(EASY|MEDIUM|HARD|EXPERT)$",
            message = "Difficulty must be one of: EASY, MEDIUM, HARD, EXPERT"
    )
    private String difficulty;

    @NotNull(message = "Please enter topic id for problem")
    private long topicId;

    @Valid
    @Size(min = 2, max = 6, message = "Please enter problem options, minimum 2 and maximum 6")
    private List<ProblemOptionUpdateDto> options;

    @Size(max = 1000, message = "Explanation cannot exceed 1000 characters")
    private String explanation;

    @NotNull(message = "Please enter category id for problem")
    private Long categoryId;
}
