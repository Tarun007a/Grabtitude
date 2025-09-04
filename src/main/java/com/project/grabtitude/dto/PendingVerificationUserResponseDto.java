package com.project.grabtitude.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class PendingVerificationUserResponseDto {
    private Long id;
    private String name;
    private String email;
}
