package com.project.grabtitude.services.impl;

import com.project.grabtitude.entity.PendingVerificationUser;
import com.project.grabtitude.entity.User;
import com.project.grabtitude.repository.PendingVerificationUserRepo;
import com.project.grabtitude.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class ScheduledJob {
    private final UserRepo userRepo;
    private final PendingVerificationUserRepo pendingVerificationUserRepo;
    public ScheduledJob(UserRepo userRepo, PendingVerificationUserRepo pendingVerificationUserRepo){
        this.userRepo = userRepo;
        this.pendingVerificationUserRepo = pendingVerificationUserRepo;
    }
    // cron format second  minute  hour  day-of-month  month  day-of-week
    // use 0 * * * * * for testing, this means every 1 minute (every time the second turn 0)
    // */10 * * * * * for every 10 seconds
    // currently using 0 0 0 * * * means midnight 12
    @Scheduled(cron = "0 0 0 * * *")
    public void resetStreak(){
        System.out.println("Streak reset running ------------------------------------------------------->");
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        List<User> users  = userRepo.findAll();
        for(User user : users){
            if(user.getLastSubmittedAt() != null && !user.getLastSubmittedAt().isAfter(yesterday)){
                user.setStreak(0);
            }
        }
        userRepo.saveAll(users);
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void deleteExpiredPendingVerificationUser(){
        log.info("Delete user running");

        LocalDate currentDate = LocalDate.now();
        List<PendingVerificationUser> pendingVerificationUsers = pendingVerificationUserRepo.findAll();
        List<PendingVerificationUser> deleteUsers = pendingVerificationUsers.stream()
                .filter(user -> user.getExpiryDate().isBefore(currentDate))
                .toList();

        log.info(deleteUsers.toString());
        if(!deleteUsers.isEmpty()) {
            pendingVerificationUserRepo.deleteAll(deleteUsers);
        }
    }
}
