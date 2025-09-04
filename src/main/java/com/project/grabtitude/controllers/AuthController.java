package com.project.grabtitude.controllers;

import com.project.grabtitude.dto.*;
import com.project.grabtitude.helper.AppConstants;
import com.project.grabtitude.mapper.impl.VerificationRequestDto;
import com.project.grabtitude.services.PendingVerificationUserService;
import com.project.grabtitude.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/")
public class AuthController {
    private final PendingVerificationUserService pendingVerificationUserService;

    private final UserService userService;

    public AuthController(PendingVerificationUserService pendingVerificationUserService,
                          UserService userService){
        this.pendingVerificationUserService = pendingVerificationUserService;
        this.userService = userService;
    }

    @PostMapping("/register-user")
    public ResponseEntity<PendingVerificationUserResponseDto> createUser(@Valid @RequestBody PendingVerificationUserRequestDto pendingVerificationUserRequestDto){
        return ResponseEntity.ok().body(pendingVerificationUserService.saveUser(pendingVerificationUserRequestDto));
    }

    //The verification link will be sent to tarun's email and admin will be verified by tarun
    @PostMapping("/register-admin")
    public ResponseEntity<PendingVerificationUserResponseDto> createAdmin(@Valid @RequestBody PendingVerificationUserRequestDto pendingVerificationUserRequestDto){
        pendingVerificationUserRequestDto.setIsAdmin(true);
        return ResponseEntity.ok().body(pendingVerificationUserService.saveUser(pendingVerificationUserRequestDto));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Boolean> verifyUser(@RequestParam(value = "token", defaultValue = AppConstants.token) String token){
        return ResponseEntity.ok().body(pendingVerificationUserService.verifyUser(token));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody OtpSendRequestDto otpSendRequestDto){
        return ResponseEntity.ok().body(userService.sendOtp(otpSendRequestDto));
    }

    @PatchMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(@Valid @RequestBody ForgetPasswordRequestDto forgetPasswordRequestDto){
        return ResponseEntity.ok().body(userService.forgetPassword(forgetPasswordRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDto userLoginDto){
        return ResponseEntity.accepted().body(userService.verify(userLoginDto));
    }
}















