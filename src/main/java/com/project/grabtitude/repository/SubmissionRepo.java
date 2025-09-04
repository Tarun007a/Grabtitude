package com.project.grabtitude.repository;

import com.project.grabtitude.entity.Submission;
import com.project.grabtitude.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission, Long> {
    List<Submission> findAllByUser(User user);
}
