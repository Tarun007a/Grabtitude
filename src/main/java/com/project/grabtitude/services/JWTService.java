package com.project.grabtitude.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    public String generateToken(String username);

    public String extractUserName(String token);

    boolean validateToken(String token, UserDetails userDetails);
}
