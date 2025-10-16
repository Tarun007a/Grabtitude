package com.project.grabtitude.services.impl;

import com.project.grabtitude.dto.*;
import com.project.grabtitude.entity.Submission;
import com.project.grabtitude.entity.User;
import com.project.grabtitude.helper.AppConstants;
import com.project.grabtitude.helper.AuthUtil;
import com.project.grabtitude.helper.ResourceNotFoundException;
import com.project.grabtitude.mapper.Mapper;
import com.project.grabtitude.repository.SubmissionRepo;
import com.project.grabtitude.repository.UserRepo;
import com.project.grabtitude.services.EmailService;
import com.project.grabtitude.services.JWTService;
import com.project.grabtitude.services.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final Mapper<User, UserRegistrationDto> userRegistrationMapper;
    private final Mapper<User, UserResponseDto> userResponseMapper;
    private final SubmissionRepo submissionRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthUtil authUtil;
    private final Random random;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    public UserServiceImpl(UserRepo userRepo, Mapper<User, UserResponseDto> userResponseMapper,
                           Mapper<User, UserRegistrationDto> userRegistrationMapper,
                           AuthUtil authUtil, BCryptPasswordEncoder passwordEncoder,
                           SubmissionRepo submissionRepo, EmailService emailService,
                           Random random, AuthenticationManager authenticationManager,
                           JWTService jwtService
    ){
        this.userRepo = userRepo;
        this.userRegistrationMapper = userRegistrationMapper;
        this.userResponseMapper = userResponseMapper;
        this.authUtil = authUtil;
        this.passwordEncoder = passwordEncoder;
        this.submissionRepo = submissionRepo;
        this.emailService = emailService;
        this.random = random;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponseDto saveAdmin(UserRegistrationDto userRegistrationDto) {
        User user = userRegistrationMapper.mapFrom(userRegistrationDto);
        user.setUserId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setStreak(0);
        user.setRole(User.Role.ADMIN);
        User savedUser = userRepo.save(user);

        return userResponseMapper.mapTo(savedUser);
    }

    @Override
    public UserResponseDto saveUser(UserRegistrationDto userRegistrationDto) {
        User user = userRegistrationMapper.mapFrom(userRegistrationDto);
        user.setUserId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setStreak(0);

        User savedUser = userRepo.save(user);

        return userResponseMapper.mapTo(savedUser);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isEmpty()) throw new UsernameNotFoundException("user with email " + email + " not found");
        return userResponseMapper.mapTo(userOptional.get());
    }

    @Override
    public UserResponseDto getUserById(String id) {
        Optional<User> userOptional = userRepo.findById(id);
        if(userOptional.isEmpty()) throw  new UsernameNotFoundException("user with id " + id + " not found");
        return userResponseMapper.mapTo(userOptional.get());
    }

    @Override
    public UserResponseDto deleteUser() {
        String email = authUtil.getEmailOfLoggedUser();
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isEmpty()) throw new UsernameNotFoundException("Please login and logout again");
        userRepo.delete(userOptional.get());
        return userResponseMapper.mapTo(userOptional.get());
    }

    @Override
    public UserResponseDto updateUser(UserRegistrationDto userRegistrationDto) {
        String email = authUtil.getEmailOfLoggedUser();
        if(!userRegistrationDto.getEmail().equals(email)) throw new AccessDeniedException("Please logout and login again");

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please login and logout again"));

        user.setName(userRegistrationDto.getName());
        user.setAbout(userRegistrationDto.getAbout());
        user.setCountry(userRegistrationDto.getCountry());
        user.setGithub(userRegistrationDto.getGithub());
        user.setInstitute(userRegistrationDto.getInstitute());
        user.setLinkedIn(userRegistrationDto.getLinkedIn());

        userRepo.save(user);
        return userResponseMapper.mapTo(user);
    }

    @Override
    public UserResponseDto updateUserProfile(ProfileUpdateDto profileUpdateDto) {
        String email = authUtil.getEmailOfLoggedUser();
        if(!profileUpdateDto.getEmail().equals(email)) throw new AccessDeniedException("Please logout and login again");

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please login and logout again"));

        user.setName(profileUpdateDto.getName());
        user.setAbout(profileUpdateDto.getAbout());
        user.setCountry(profileUpdateDto.getCountry());
        user.setGithub(profileUpdateDto.getGithub());
        user.setInstitute(profileUpdateDto.getInstitute());
        user.setLinkedIn(profileUpdateDto.getLinkedIn());

        userRepo.save(user);
        return userResponseMapper.mapTo(user);
    }

    @Override
    public Integer getCurrentStreak() {
        String email = authUtil.getEmailOfLoggedUser();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found, please logout and try loggin again"));
        return user.getStreak();
    }

    // For this we have to fetch users previous submissions and then we need to extract the
    // problems for the submissions then return
    @Override
    public List<ProblemResponseDto> getPreviousProblems(int previousProblems) {
        return null;
    }

    @Override
    public Integer getMaxStreak() {
        String email = authUtil.getEmailOfLoggedUser();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found, please logout and try loggin again"));
        return user.getMaxStreak();
    }

    @Override
    public String verify(UserLoginDto userLoginDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword()));
        String token = jwtService.generateToken(userLoginDto.getUsername());
        return token;
    }

    @Override
    public String getOtp() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < AppConstants.otpSize; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }

    @Override
    public String sendOtp(OtpSendRequestDto otpSendRequestDto) {
        String email = otpSendRequestDto.getEmail();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist with email : " + email));
        if(user.getOtpSentDate().isBefore(LocalDate.now())){
            user.setOtpSentDate(LocalDate.now());
            user.setOtpSent(0);
        }
        if(user.getOtpSent() >= AppConstants.maxOtoLimit) {
            throw new AccessDeniedException("You have exceeded the maximum limit of otp, please try again tomorrow");
        }
        String otp = this.getOtp();
        emailService.sendOtpEmail(email, otp);
        user.setOtpSent(user.getOtpSent()+1);
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusSeconds(300L));
        userRepo.save(user);

        return "OTP sent successfully and is only valid for 5 minutes";
    }

    @Override
    public String forgetPassword(ForgetPasswordRequestDto forgetPasswordRequestDto) {
        String email = forgetPasswordRequestDto.getEmail();
        String otp = forgetPasswordRequestDto.getOtp();
        String newPassword = forgetPasswordRequestDto.getNewPassword();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist with email : " + email));

        if(!user.getOtp().equals(otp)) throw new RuntimeException("Please enter correct otp");
        if(user.getOtpExpiry().isBefore(LocalDateTime.now())) throw new RuntimeException("OTP expired try again with a new otp");

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        return AppConstants.otpResetForgetPasswordReturnMessage;
    }

    @Override
    public String resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        String email = authUtil.getEmailOfLoggedUser();
        if(email.isEmpty() || email.isBlank()) throw new AccessDeniedException("Please login to continue");

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No user found with the email : " + email));

        if(!passwordEncoder.matches(resetPasswordRequestDto.getOldPassword(), user.getPassword())){
            return "Please enter correct password";
        }
        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        userRepo.save(user);
        return "Password changed successfully";
    }

    @Override
    public UserResponseDto saveUser(User user) {
        User savedUser = userRepo.save(user);
        return userResponseMapper.mapTo(savedUser);
    }

    @Override
    public ProfileResponseDto getProfile(String id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Please login and logout again"));

        List<Submission> submissions = submissionRepo.findAllByUser(user);
        Set<Long> uniqueQuestions = new HashSet<>();
        Map<String, Integer> difficultyWiseQuestionsSolved = new HashMap<>();
        Map<String, Integer> topicWiseQuestionsSolved = new HashMap<>();
        double correctSubmissions = 0;

        for(Submission submission : submissions){
            if(submission.isCorrect() && !uniqueQuestions.contains(submission.getProblem().getProblemId())){
                uniqueQuestions.add(submission.getProblem().getProblemId());
                String difficulty = submission.getProblem().getDifficulty().toString();
                difficultyWiseQuestionsSolved.put(difficulty, difficultyWiseQuestionsSolved.getOrDefault(difficulty, 0)+1);

                String topic = submission.getProblem().getTopic().toString();
                topicWiseQuestionsSolved.put(topic, topicWiseQuestionsSolved.getOrDefault(topic, 0)+1);
                correctSubmissions++;
            }
        }

        double accuracy = (correctSubmissions*100) / (double) submissions.size();
        ProfileResponseDto profileResponseDto = new ProfileResponseDto();
        profileResponseDto.setName(user.getName());
        profileResponseDto.setUserId(user.getUserId());
        profileResponseDto.setAbout(user.getAbout());
        profileResponseDto.setCountry(user.getCountry());
        profileResponseDto.setInstitute(user.getInstitute());
        profileResponseDto.setLinkedIn(user.getLinkedIn());
        profileResponseDto.setGithub(user.getGithub());
        profileResponseDto.setDifficultyLevelWiseQuestionsSolved(difficultyWiseQuestionsSolved);
        profileResponseDto.setTopicWiseQuestionsSolved(topicWiseQuestionsSolved);
        profileResponseDto.setQuestionsSolved(uniqueQuestions.size());
        profileResponseDto.setAccuracy(accuracy);

        return profileResponseDto;
    }

    @Override
    public ProfileResponseDto getCurrentUserProfile() {
        String currentUserEmail = authUtil.getEmailOfLoggedUser();
        User currentUser = userRepo.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        return getProfile(currentUser.getUserId());
    }

    @Override
    public Boolean isLoggedUserAdmin() {
        String email = authUtil.getEmailOfLoggedUser();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
        return user.getRole() == User.Role.ADMIN;
    }

    @Override
    public Long getTotalUsers() {
        return userRepo.count();
    }
}


