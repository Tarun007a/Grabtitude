package com.project.grabtitude.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String userId;
    private String name;
    private String email;
    private String institute;
    private String country;
    private String linkedIn;
    private String github;
    private int streak;
    private String about;
}
