package com.project.grabtitude.controllers;

import com.project.grabtitude.dto.ProfileResponseDto;
import com.project.grabtitude.dto.UserResponseDto;
import com.project.grabtitude.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Tag(name = "Root Controller", description = "General endpoints for health check, home, and user profiles")
public class RootController {
    private final UserService userService;
    
    public RootController(UserService userService){
        this.userService = userService;
    }

    @Operation(
            summary = "Redirect root to /home",
            description = "Redirects the user to `/home` endpoint."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirected to /home")
    })
    @GetMapping("/")
    public void home(HttpServletResponse response) throws IOException {
        response.sendRedirect("/home");
    }
    @Operation(
            summary = "Home Page",
            description = "Returns a simple message for the home page."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Home page response returned")
    })
    @GetMapping("/home")
    public String homePage(){
        return "home page view";
    }

    @Operation(summary = "Test API Connection", description = "Returns a test response to verify server connectivity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connection successful")
    })
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok().body("Test connection");
    }

    @Operation(summary = "Get User Profile", description = "Fetches a user profile by the given user ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User profile not found")
    })
    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileResponseDto> getUserProfile(@PathVariable String id){
        return ResponseEntity.ok().body(userService.getProfile(id));
    }

    @Operation(summary = "Get User by Email", description = "Fetches user details using their email address.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/get-user/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok().body(userService.getUserByEmail(email));
    }

    @Operation(summary = "Get User by ID", description = "Fetches user details using their unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/get-user-id/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String id){
        UserResponseDto user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
