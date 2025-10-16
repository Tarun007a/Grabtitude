package com.project.grabtitude.services;

import com.project.grabtitude.entity.User;
import com.project.grabtitude.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private UserRepo userRepo;
    public MyUserDetailsService(UserRepo userRepo){
        this.userRepo = userRepo;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepo.findByEmail(username);
        if(userOptional.isEmpty()) throw new UsernameNotFoundException("user not found with user name" + username);
        return userOptional.get();
    }
}
