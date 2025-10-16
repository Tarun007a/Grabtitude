package com.project.grabtitude.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TopicResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
}
