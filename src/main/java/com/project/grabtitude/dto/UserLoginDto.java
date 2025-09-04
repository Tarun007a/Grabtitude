package com.project.grabtitude.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserLoginDto {
    @NotNull(message = "Please enter username")
    @Size(min = 2, max = 50, message = "Name length should be between 2 to 50 characters")
    private String username;

    @NotNull(message = "Please enter size")
    @Size(min = 8, max = 12, message = "Password length should be between 8 to 12")
    private String password;
}
