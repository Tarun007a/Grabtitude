package com.project.grabtitude.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProblemOptionRequestDto {
    @NotNull(message = "Please enter correctness of option, either true or false")
    private Boolean correct;

    @NotNull(message = "Please enter the content of option")
    @Size(min = 1, max = 500, message = "The content of each message should be minimum 1 and maximum 500 character length")
    private String content;
}