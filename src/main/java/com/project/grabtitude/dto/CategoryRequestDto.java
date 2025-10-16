package com.project.grabtitude.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDto {
    @NotNull(message = "Name cannot be empty")
    @Size(min = 2, max = 100, message = "Category name should be 2 to 100 characters")
    private String name;

    @NotNull(message = "Description cannot be empty")
    @Size(min = 2, max = 1000, message = "Category description should be 2 to 1000 characters")
    private String description;
}
