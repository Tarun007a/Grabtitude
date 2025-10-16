package com.project.grabtitude.config;

import com.project.grabtitude.entity.User;
import com.project.grabtitude.repository.UserRepo;
import com.project.grabtitude.services.JWTService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Temporary code needs to be written again
@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepo userRepo;
    private final JWTService jwtService;

    public OAuthAuthenticationSuccessHandler(UserRepo userRepo, JWTService jwtService){
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication
                                        ) throws IOException, ServletException {

        System.out.println("-------------------------GOOGLE LOGIN SUCCESSFUL------------------------------");

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();


        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        if (name == null) name = oAuth2User.getName();
        Optional<User> userOptional = userRepo.findByEmail(email);

        if(userOptional.isEmpty()){
            String id = UUID.randomUUID().toString();
            String password = UUID.randomUUID().toString();
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setAuthProvider(User.AuthProvider.GOOGLE);
            user.setPassword(password);
            user.setUserId(id);
            userRepo.save(user);
        }
        Optional<User> savedUserOptional = userRepo.findByEmail(email);
        if(savedUserOptional.isEmpty()) throw new RuntimeException("Please try again, no user found saved for email : " + email);

        String token = jwtService.generateToken(savedUserOptional.get().getUsername());

        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24*60*60); // 1 day
        response.addCookie(cookie);

        response.sendRedirect("/user/");
    }
}
