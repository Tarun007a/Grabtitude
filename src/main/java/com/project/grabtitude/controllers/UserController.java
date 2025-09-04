package com.project.grabtitude.controllers;

import com.project.grabtitude.dto.*;
import com.project.grabtitude.services.ProblemService;
import com.project.grabtitude.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user/")
public class UserController {
    private final UserService userServices;
    private final ProblemService problemService;
    public UserController(UserService userServices, ProblemService problemService){
        this.userServices = userServices;
        this.problemService = problemService;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<UserResponseDto> deleteUser(){
        UserResponseDto user = userServices.deleteUser();
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto){
        UserResponseDto user = userServices.updateUser(userRegistrationDto);
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmissionResponseDto> submitOption(@RequestBody SubmissionRequestDto submissionRequestDto){
        SubmissionResponseDto submissionResponseDto = problemService.submit(submissionRequestDto);
        return new ResponseEntity<>(submissionResponseDto, HttpStatus.OK);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto){
        return ResponseEntity.ok().body(userServices.resetPassword(resetPasswordRequestDto));
    }

    @GetMapping("/")
    public String userHome(){
        return "user home page view";
    }
}
