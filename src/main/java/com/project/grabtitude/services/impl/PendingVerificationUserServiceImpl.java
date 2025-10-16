package com.project.grabtitude.services.impl;

import com.project.grabtitude.dto.PendingVerificationUserRequestDto;
import com.project.grabtitude.dto.PendingVerificationUserResponseDto;
import com.project.grabtitude.entity.PendingVerificationUser;
import com.project.grabtitude.entity.User;
import com.project.grabtitude.helper.AppConstants;
import com.project.grabtitude.mapper.impl.PendingVerificationUserRequestMapper;
import com.project.grabtitude.mapper.impl.PendingVerificationUserResponseMapper;
import com.project.grabtitude.repository.PendingVerificationUserRepo;
import com.project.grabtitude.repository.UserRepo;
import com.project.grabtitude.services.EmailService;
import com.project.grabtitude.services.PendingVerificationUserService;
import com.project.grabtitude.services.UserService;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Log
@Service
public class PendingVerificationUserServiceImpl implements PendingVerificationUserService {

    private final PendingVerificationUserRepo pendingVerificationUserRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final PendingVerificationUserResponseMapper pendingVerificationUserResponseMapper;
    private final UserService userService;
    private final PendingVerificationUserRequestMapper pendingVerificationUserRequestMapper;
    private final EmailService emailService;
    public PendingVerificationUserServiceImpl(PendingVerificationUserRepo pendingVerificationUserRepo,
                                              PendingVerificationUserRequestMapper pendingVerificationUserRequestMapper,
                                              PendingVerificationUserResponseMapper pendingVerificationUserResponseMapper,
                                              UserService userService, EmailService emailService,
                                              UserRepo userRepo, PasswordEncoder passwordEncoder){
        this.pendingVerificationUserRepo = pendingVerificationUserRepo;
        this.pendingVerificationUserRequestMapper = pendingVerificationUserRequestMapper;
        this.pendingVerificationUserResponseMapper = pendingVerificationUserResponseMapper;
        this.userService = userService;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public PendingVerificationUserResponseDto saveUser(PendingVerificationUserRequestDto pendingVerificationUserRequestDto) {
        //If the pending user is already saved and request comes again so just update it with password
        String email = pendingVerificationUserRequestDto.getEmail();

        //If the email is already registered with some user who is in user db(the user who is verified) so don't save this
        if(userRepo.findByEmail(email).isPresent()){
            throw new RuntimeException("The email is already registered try login with you email and password");
        }

        if(this.findByEmail(email).isPresent()){
            log.info("PendingVerificationUser already exist so calling update for it");
            return this.updateUser(pendingVerificationUserRequestDto);
        }

        PendingVerificationUser pendingVerificationUser = pendingVerificationUserRequestMapper.mapFrom(pendingVerificationUserRequestDto);
        String token = UUID.randomUUID().toString();
        LocalDate currentDate = LocalDate.now();
        LocalDate expiryDate = currentDate.plusDays(3);
        pendingVerificationUser.setVerificationToken(token);
        pendingVerificationUser.setExpiryDate(expiryDate);
        pendingVerificationUser.setPassword(passwordEncoder.encode(pendingVerificationUser.getPassword()));

        // set true for admin
        String emailTo = pendingVerificationUser.getEmail();
        if(pendingVerificationUserRequestDto.getIsAdmin() != null && pendingVerificationUserRequestDto.getIsAdmin()){
            pendingVerificationUser.setIsAdmin(true);
            emailTo = AppConstants.adminVerificationMail;
        }

        emailService.sendVerificationEmail(emailTo, AppConstants.DOMAIN+"/auth/verify-email?token=" + token);
        PendingVerificationUser savedUser = pendingVerificationUserRepo.save(pendingVerificationUser);
        return pendingVerificationUserResponseMapper.mapTo(savedUser);
    }

    @Override
    public void saveVerifiedUser(PendingVerificationUser pendingVerificationUser) {
        User user = new User();
        String id = UUID.randomUUID().toString();

        user.setUserId(id);
        user.setName(pendingVerificationUser.getName());
        user.setEmail(pendingVerificationUser.getEmail());
        user.setPassword(pendingVerificationUser.getPassword());

        if(pendingVerificationUser.getIsAdmin() != null
            && pendingVerificationUser.getIsAdmin()) {
            user.setRole(User.Role.ADMIN);   // set if admin
        }

        userService.saveUser(user);
    }

    @Override
    public PendingVerificationUserResponseDto updateUser(PendingVerificationUserRequestDto pendingVerificationUserRequestDto) {
        //although this will be only called if the user already exist as we call this by save user still checking again
        String email = pendingVerificationUserRequestDto.getEmail();


        PendingVerificationUser existingUser = pendingVerificationUserRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user exist with email which you are trying to update"));
        LocalDate currentDate = LocalDate.now();
        LocalDateTime currDateAndTime = LocalDateTime.now();
        if(existingUser.getNextOtpTime().isAfter(currDateAndTime)){
            throw new RuntimeException("You can send your next otp after " + existingUser.getNextOtpTime());
        }
        LocalDate newExpiryDate = currentDate.plusDays(3);
        String token = UUID.randomUUID().toString();

        existingUser.setName(pendingVerificationUserRequestDto.getName());
        existingUser.setPassword(passwordEncoder.encode(pendingVerificationUserRequestDto.getPassword()));
        existingUser.setExpiryDate(newExpiryDate);
        existingUser.setVerificationToken(token);

        if(pendingVerificationUserRequestDto.getIsAdmin() != null && pendingVerificationUserRequestDto.getIsAdmin()){
            existingUser.setIsAdmin(true);
            email = AppConstants.adminVerificationMail;
        }

        emailService.sendVerificationEmail(email, AppConstants.DOMAIN+"/auth/verify-email?token=" + token);
        existingUser.setNextOtpTime(currDateAndTime.plusSeconds(300));
        PendingVerificationUser savedUser = pendingVerificationUserRepo.save(existingUser);

        return pendingVerificationUserResponseMapper.mapTo(savedUser);
    }

    @Override
    public Optional<PendingVerificationUser> findByEmail(String email) {
        return pendingVerificationUserRepo.findByEmail(email);
    }

    @Override
    public boolean verifyUser(String token) {
        PendingVerificationUser user = pendingVerificationUserRepo.findByVerificationToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("Please pass the correct token for verificaion"));

        pendingVerificationUserRepo.delete(user);
        this.saveVerifiedUser(user);
        return true;
    }
}
