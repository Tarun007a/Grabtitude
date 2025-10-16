package com.project.grabtitude.controllers;

import com.project.grabtitude.dto.*;
import com.project.grabtitude.helper.AppConstants;
import com.project.grabtitude.mapper.impl.VerificationRequestDto;
import com.project.grabtitude.services.PendingVerificationUserService;
import com.project.grabtitude.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/")
@Tag(name = "Authentication Controller", description = "APIs for user registration, login, email verification, OTP and password management")
public class AuthController {
    private final PendingVerificationUserService pendingVerificationUserService;

    private final UserService userService;

    public AuthController(PendingVerificationUserService pendingVerificationUserService,
                          UserService userService){
        this.pendingVerificationUserService = pendingVerificationUserService;
        this.userService = userService;
    }

    @Operation(
            summary = "Register a new User",
            description = "Registers a new user. The user will receive an email verification link before account activation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = PendingVerificationUserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/register-user")
    public ResponseEntity<PendingVerificationUserResponseDto> createUser(@Valid @RequestBody PendingVerificationUserRequestDto pendingVerificationUserRequestDto){
        pendingVerificationUserRequestDto.setIsAdmin(false);
        return ResponseEntity.ok().body(pendingVerificationUserService.saveUser(pendingVerificationUserRequestDto));
    }

    @Operation(
            summary = "Register a new Admin",
            description = "Registers a new admin user. Admin verification will be handled manually by the system owner."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin registered successfully",
                    content = @Content(schema = @Schema(implementation = PendingVerificationUserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    //The verification link will be sent to tarun's email and admin will be verified by tarun
    @PostMapping("/register-admin")
    public ResponseEntity<PendingVerificationUserResponseDto> createAdmin(@Valid @RequestBody PendingVerificationUserRequestDto pendingVerificationUserRequestDto){
        pendingVerificationUserRequestDto.setIsAdmin(true);
        return ResponseEntity.ok().body(pendingVerificationUserService.saveUser(pendingVerificationUserRequestDto));
    }

    @Operation(
            summary = "Verify User Email",
            description = "Verifies a user’s email using a token sent in the verification link."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    @GetMapping("/verify-email")
    public ResponseEntity<Boolean> verifyUser(@RequestParam(value = "token", defaultValue = AppConstants.token) String token){
        return ResponseEntity.ok().body(pendingVerificationUserService.verifyUser(token));
    }

    @Operation(
            summary = "Send OTP",
            description = "Sends a one-time password (OTP) to the user’s registered email for verification or password reset."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody OtpSendRequestDto otpSendRequestDto){
        return ResponseEntity.ok().body(userService.sendOtp(otpSendRequestDto));
    }

    @Operation(
            summary = "Forget Password",
            description = "Allows a user to reset their password using OTP verification."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or OTP")
    })
    @PatchMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(@Valid @RequestBody ForgetPasswordRequestDto forgetPasswordRequestDto){
        return ResponseEntity.ok().body(userService.forgetPassword(forgetPasswordRequestDto));
    }

    @Operation(
            summary = "Login",
            description = "Authenticates a user with email and password. Returns a JWT token if successful."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDto userLoginDto){
        return ResponseEntity.accepted().body(userService.verify(userLoginDto));
    }
}















