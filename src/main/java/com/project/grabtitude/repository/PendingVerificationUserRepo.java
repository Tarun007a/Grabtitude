package com.project.grabtitude.repository;

import com.project.grabtitude.entity.PendingVerificationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PendingVerificationUserRepo extends JpaRepository<PendingVerificationUser, Long> {
    Optional<PendingVerificationUser> findByEmail(String email);

    Optional<PendingVerificationUser> findByVerificationToken(String verificationToken);
}
