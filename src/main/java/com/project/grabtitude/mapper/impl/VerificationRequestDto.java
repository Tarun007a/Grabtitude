package com.project.grabtitude.mapper.impl;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationRequestDto {
    @NotNull(message = "Please enter the token")
    @Size(min = 36, max = 36, message = "The token length should be 36 characters, please click resend email and use new verification link")
    private String token;

    @NotNull(message = "Please enter the email")
    @Email(message = "Please enter a valid email address")
    private String email;
}
