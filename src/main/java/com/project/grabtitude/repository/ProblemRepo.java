package com.project.grabtitude.repository;

import com.project.grabtitude.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepo extends JpaRepository<Problem, Long> {
    Page<Problem> findAll(Pageable pageable);
    Page<Problem> findByTitleContaining(String title, Pageable pageable);
}
