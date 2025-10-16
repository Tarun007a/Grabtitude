package com.project.grabtitude.dto;

import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Service;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequestDto {
    @NotNull(message = "Please enter old password")
    @Size(min = 8, max = 12, message = "Please enter 8 to 12 characters for old password")
    private String oldPassword;

    @NotNull(message = "Please enter new Password")
    @Size(min = 8, max = 12, message = "Please enter 8 to 12 characters for new password")
    private String newPassword;
}
