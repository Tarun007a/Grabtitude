package com.project.grabtitude.config;


import com.project.grabtitude.filters.JWTFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;


@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@Log
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler;
    private final OAuthAuthenticationFailureHandler oAuthAuthenticationFailureHandler;
    private final JWTFilter jwtFilter;
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    public SecurityConfig(UserDetailsService userDetailsService,
                          OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler,
                        OAuthAuthenticationFailureHandler oAuthAuthenticationFailureHandler,
                          JWTFilter jwtFilter){
        this.userDetailsService = userDetailsService;
        this.oAuthAuthenticationSuccessHandler = oAuthAuthenticationSuccessHandler;
        this.oAuthAuthenticationFailureHandler = oAuthAuthenticationFailureHandler;
        this.jwtFilter = jwtFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.cors(Customizer.withDefaults());

        httpSecurity.csrf(customizer -> customizer.disable());

        httpSecurity.authorizeHttpRequests(request -> {
            request.requestMatchers("/api-docs/**", "/swagger-ui/**", "/docs/**").permitAll();
            request.requestMatchers("/admin/**").hasAnyRole("ADMIN");
            request.requestMatchers("/user/**").authenticated();
            request.anyRequest().permitAll();
        });

        httpSecurity.userDetailsService(userDetailsService);
        // remove in production
        httpSecurity.httpBasic(Customizer.withDefaults());

        // add jwt filter in filter chain
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        httpSecurity.oauth2Login((oauth) -> {
            oauth.successHandler(oAuthAuthenticationSuccessHandler);
            oauth.failureHandler(oAuthAuthenticationFailureHandler);
            oauth.loginPage("/login");
        });

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig){
        try {
            return authConfig.getAuthenticationManager();
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Exception while configuring auth manager bean");
        }
    }

    @Bean
    public Random random(){
        return new SecureRandom();
    }

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Grabtitude API").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

}
