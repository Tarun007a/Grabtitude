package com.project.grabtitude.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OtpSendRequestDto {
    @NotNull(message = "Please enter email address")
    @Email(message = "Please enter a valid email address")
    private String email;
}
