package com.project.grabtitude.services;

import com.project.grabtitude.dto.*;
import com.project.grabtitude.entity.User;

import java.util.List;

public interface UserService {
    UserResponseDto saveUser(UserRegistrationDto userRegistrationDto);

    UserResponseDto updateUser(UserRegistrationDto userRegistrationDto);

    UserResponseDto updateUserProfile(ProfileUpdateDto profileUpdateDto);

    UserResponseDto getUserByEmail(String email);

    UserResponseDto getUserById(String id);

    UserResponseDto deleteUser();

    UserResponseDto saveAdmin(UserRegistrationDto userRegistrationDto);

    ProfileResponseDto getProfile(String id);

    ProfileResponseDto getCurrentUserProfile();

    UserResponseDto saveUser(User user);

    String resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);

    String forgetPassword(ForgetPasswordRequestDto forgetPasswordRequestDto);

    String sendOtp(OtpSendRequestDto otpSendRequestDto);

    String getOtp();

    String verify(UserLoginDto userLoginDto);

    Integer getCurrentStreak();

    Integer getMaxStreak();

    List<ProblemResponseDto> getPreviousProblems(int previousProblems);

    Boolean isLoggedUserAdmin();

    Long getTotalUsers();
}
