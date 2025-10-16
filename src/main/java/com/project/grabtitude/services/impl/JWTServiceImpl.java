package com.project.grabtitude.services.impl;

import com.project.grabtitude.services.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTServiceImpl implements JWTService {
    // fix this in prod
    private String secretKey = "";
    public JWTServiceImpl(){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey generatedKey = keyGenerator.generateKey();
            secretKey = Base64.getEncoder().encodeToString(generatedKey.getEncoded());
        }
        catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }
    private SecretKey getKey(){
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        }
        catch (Exception e){
            throw new AccessDeniedException("Expired token");
        }
    }

    private Date extractExpiration(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    @Override
    public String extractUserName(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build().parseSignedClaims(token)
                    .getPayload();
        }
        catch (Exception e){
            throw new AccessDeniedException("Invalid token");
        }
    }

    @Override
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60*24))
                .signWith(getKey())
                .compact();
    }
}
