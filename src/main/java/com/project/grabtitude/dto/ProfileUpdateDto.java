package com.project.grabtitude.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileUpdateDto {
    @Size(min = 2, max = 50, message = "Name length should be between 2 to 50 characters")
    private String name;

    @Email(message = "Please enter a valid email address")
    private String email;

    @Size(max = 100, message = "Institute name can be maximum 100 characters")
    private String institute;

    @Size(max = 100, message = "Country name can be maximum 100 characters")
    private String country;

    @Size(max = 100, message = "Linkedin url can be maximum 100 characters")
    private String linkedIn;

    @Size(max = 100, message = "Github url can be maximum 100 characters")
    private String github;

    @Size(max = 1000, message = "About section can be maximum 1000 character")
    private String about;
}