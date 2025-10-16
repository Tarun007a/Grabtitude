package com.project.grabtitude.repository;

import com.project.grabtitude.entity.Problem;
import com.project.grabtitude.entity.ProblemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemOptionRepo extends JpaRepository<ProblemOption, Long> {
    List<ProblemOption> findAllByProblem(Problem problem);
}
