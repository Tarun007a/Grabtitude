package com.project.grabtitude.dto;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemOptionResponseDto {
    private Long id;
    private String content;
    private boolean correct;
}
