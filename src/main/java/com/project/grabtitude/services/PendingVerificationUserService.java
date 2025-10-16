package com.project.grabtitude.services;

import com.project.grabtitude.dto.PendingVerificationUserRequestDto;
import com.project.grabtitude.dto.PendingVerificationUserResponseDto;
import com.project.grabtitude.entity.PendingVerificationUser;
import com.project.grabtitude.mapper.impl.VerificationRequestDto;

import java.util.Optional;

public interface PendingVerificationUserService {
    public PendingVerificationUserResponseDto saveUser(PendingVerificationUserRequestDto pendingVerificationUserRequestDto);

    public boolean verifyUser(String token);

    public void saveVerifiedUser(PendingVerificationUser pendingVerificationUser);

    public Optional<PendingVerificationUser> findByEmail(String email);

    public PendingVerificationUserResponseDto updateUser(PendingVerificationUserRequestDto pendingVerificationUserRequestDto);
}
