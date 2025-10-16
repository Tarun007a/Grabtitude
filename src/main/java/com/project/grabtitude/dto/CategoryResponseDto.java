package com.project.grabtitude.dto;

import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;
}
