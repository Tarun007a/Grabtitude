package com.project.grabtitude.controllers;

import com.project.grabtitude.dto.ProfileResponseDto;
import com.project.grabtitude.dto.UserResponseDto;
import com.project.grabtitude.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class RootController {
    private final UserService userService;
    public RootController(UserService userService){
        this.userService = userService;
    }
    @GetMapping("/")
    public void home(HttpServletResponse response) throws IOException {
        response.sendRedirect("/home");
    }
    @GetMapping("/home")
    public String homePage(){
        return "home page view";
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok().body("Test connection");
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileResponseDto> getUserProfile(@PathVariable String id){
        return ResponseEntity.ok().body(userService.getProfile(id));
    }

    @GetMapping("/get-user/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok().body(userService.getUserByEmail(email));
    }

    @GetMapping("/get-user-id/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String id){
        UserResponseDto user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
