package com.project.grabtitude.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class PendingVerificationUserRequestDto {
    @NotNull(message = "Please enter your name")
    @Size(min = 2, max = 50, message = "Name size must be between 2 to 50 character")
    private String name;

    @NotNull(message = "Please enter your password")
    @Size(min = 8, max = 12, message = "Password must of size 8 to 12")
    private String password;

    @NotNull(message = "Please enter your email")
    @Email(message = "Please enter a valid email address")
    private String email;

    private Boolean isAdmin;
}
