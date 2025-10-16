package com.project.grabtitude.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PendingVerificationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String verificationToken;

    private int otpSent = 0;

    private LocalDate otpSendDate = LocalDate.now();

    private LocalDateTime nextOtpTime = LocalDateTime.now();

    private LocalDate expiryDate = LocalDate.now();

    private Boolean isAdmin = false;
}
