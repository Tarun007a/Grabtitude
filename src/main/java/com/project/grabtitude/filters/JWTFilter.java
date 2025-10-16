package com.project.grabtitude.filters;

import com.project.grabtitude.services.JWTService;
import com.project.grabtitude.services.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final ApplicationContext applicationContext;
    public JWTFilter(JWTService jwtService, ApplicationContext applicationContext){
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if(authHeader != null && authHeader.startsWith("Bearer")){
            token = authHeader.substring(7);
            try {
                username = jwtService.extractUserName(token);
            }
            catch (Exception e){
                System.out.println("Invalid token : " + token);
            }
        }

        // Reading from cookie as when oauth2 set this,
        if (token == null && request.getCookies() != null) {
            Cookie jwtCookie = null;
            for(Cookie cookie : request.getCookies()){
                if(cookie.getName().equals("JWT")){
                    jwtCookie = cookie;
                    break;
                }
            }
            if (jwtCookie != null) {
                token = jwtCookie.getValue();
                System.out.println("Found in cookie------>" + token);
                try {
                    username = jwtService.extractUserName(token);
                }
                catch (Exception e){
                    System.out.println("Invalid token : " + token);
                }
            }
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = applicationContext.getBean(MyUserDetailsService.class).loadUserByUsername(username);
            if(jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
