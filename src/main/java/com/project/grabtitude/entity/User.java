package com.project.grabtitude.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private int streak = 0;

    private int maxStreak = 0;

    private String institute;

    private String country;

    private String linkedIn;

    private String github;

    private String otp;

    private LocalDateTime otpExpiry;

    private int otpSent;

    private LocalDate otpSentDate;

    @Column(length = 1000)
    private String about;

    private LocalDate lastSubmittedAt;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;      //setting default role as USER

    @PrePersist
    public void prePersist() {               // set defaults when user is saved in db
        this.createdAt = LocalDateTime.now();
        this.otpSent = 0;
        this.otpExpiry = LocalDateTime.now();
        this.otpSentDate = LocalDate.now();
    }

    public enum Role {
        ADMIN, USER
    }

    public enum AuthProvider{
        LOCAL, GOOGLE
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //this will convert only the assigned role into simpleGrantedAuthority
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    private boolean enabled = true;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked = true;

    private boolean credentialsNonExpired = true;

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled(){
        return this.enabled;
    }

    @Override
    public boolean isAccountNonExpired(){
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked(){
        return this.accountNonLocked;
    }
}

