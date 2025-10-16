package com.project.grabtitude.controllers;

import com.project.grabtitude.dto.*;
import com.project.grabtitude.helper.AppConstants;
import com.project.grabtitude.helper.CustomPageResponse;
import com.project.grabtitude.services.ProblemService;
import com.project.grabtitude.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user/")
@Tag(name = "User Controller", description = "Endpoints for managing user accounts and submissions")
public class UserController {
    private final UserService userServices;
    private final ProblemService problemService;
    public UserController(UserService userServices, ProblemService problemService){
        this.userServices = userServices;
        this.problemService = problemService;
    }

    @Operation(summary = "Delete Current User", description = "Deletes the logged-in user's account.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "User deleted successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized or invalid token")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<UserResponseDto> deleteUser(){
        UserResponseDto user = userServices.deleteUser();
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Update User Profile", description = "Updates the logged-in user's profile information without changing password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDto> updateUserProfile(@Valid @RequestBody ProfileUpdateDto profileUpdateDto){
        UserResponseDto user = userServices.updateUserProfile(profileUpdateDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Update User", description = "Updates the logged-in user's details using the provided registration data.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/update")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto){
        UserResponseDto user = userServices.updateUser(userRegistrationDto);
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }


    @Operation(summary = "Submit Problem Option", description = "Submits a solution option for a problem and returns the submission result.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submission processed successfully",
                    content = @Content(schema = @Schema(implementation = SubmissionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid submission data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/submit")
    public ResponseEntity<SubmissionResponseDto> submitOption(@RequestBody SubmissionRequestDto submissionRequestDto){
        SubmissionResponseDto submissionResponseDto = problemService.submit(submissionRequestDto);
        return new ResponseEntity<>(submissionResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "Reset User Password", description = "Resets the password of the logged-in user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto){
        return ResponseEntity.ok().body(userServices.resetPassword(resetPasswordRequestDto));
    }

    @Operation(summary = "Get Current User Profile", description = "Returns the current logged-in user's profile with complete details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getCurrentUserProfile(){
        ProfileResponseDto profile = userServices.getCurrentUserProfile();
        return ResponseEntity.ok().body(profile);
    }

    @Operation(summary = "User Home Page", description = "Returns a simple message for the user home page.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User home page retrieved successfully")
    })
    @GetMapping("/")
    public String userHome(){
        return "user home page view";
    }

    @GetMapping("/get-streak")
    public ResponseEntity<Integer> getCurrentStreak(){
        return ResponseEntity.ok().body(userServices.getCurrentStreak());
    }

    @GetMapping("/get-max-streak")
    public ResponseEntity<Integer> getMaxStreak(){
        return ResponseEntity.ok().body(userServices.getMaxStreak());
    }

    @GetMapping("/get-recent-activity")
    public ResponseEntity<List<ProblemResponseDto>> getRecentActivity(
           @RequestParam(value = "problems", defaultValue = AppConstants.previousProblems) int previousProblems
    ){
        return ResponseEntity.ok().body(userServices.getPreviousProblems(previousProblems));
    }

    @GetMapping("/get-total-problems-by-user")
    public ResponseEntity<Integer> getTotalProblemsByUser(){
        return ResponseEntity.ok().body(problemService.getTotalProblemsByUser());
    }

    @GetMapping("/is-admin")
    public ResponseEntity<Boolean> getIsUserAdmin(){
        return ResponseEntity.ok().body(userServices.isLoggedUserAdmin());
    }
}
