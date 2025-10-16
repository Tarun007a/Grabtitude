package com.project.grabtitude.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordRequestDto {
    @NotNull(message = "Please enter the email")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotNull(message = "Please enter otp")
    @Size(min = 6, max = 8, message = "Please enter the correct OTP")
    private String otp;

    @NotNull(message = "Please enter the newPassword")
    @Size(min = 8, max = 12, message = "The password must be between ")
    private String newPassword;
}
