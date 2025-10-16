package com.project.grabtitude.repository;

import com.project.grabtitude.entity.Category;
import com.project.grabtitude.entity.Problem;
import com.project.grabtitude.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepo extends JpaRepository<Problem, Long> {
    Page<Problem> findAll(Pageable pageable);
    Page<Problem> findByTitleContaining(String title, Pageable pageable);
    Page<Problem> findByTopic(Topic topic, Pageable pageable);
    Page<Problem> findByDifficulty(Problem.Difficulty difficulty, Pageable pageable);
    Page<Problem> findByCategory(Category category, Pageable pageable);
    Page<Problem> findByTopicAndDifficulty(Topic topic, Problem.Difficulty difficulty, Pageable pageable);


    @Query("""
    SELECT p
    FROM Problem p
    WHERE (:difficulty IS NULL OR p.difficulty = :difficulty)
      AND (:topic IS NULL OR p.topic = :topic)
      AND (
          :status IS NULL
          OR (
              :status = 'solved'
              AND EXISTS (
                  SELECT 1 FROM Submission s
                  WHERE s.problem = p AND s.user.userId = :userId AND s.isCorrect = true
              )
          )
          OR (
              :status = 'attempted'
              AND EXISTS (
                  SELECT 1 FROM Submission s
                  WHERE s.problem = p AND s.user.userId = :userId
              )
              AND NOT EXISTS (
                  SELECT 1 FROM Submission s
                  WHERE s.problem = p AND s.user.userId = :userId AND s.isCorrect = true
              )
          )
          OR (
              :status = 'unsolved'
              AND NOT EXISTS (
                  SELECT 1 FROM Submission s
                  WHERE s.problem = p AND s.user.userId = :userId
              )
          )
      )
    """)
    Page<Problem> findProblemsByFilters(
            @Param("difficulty") Problem.Difficulty difficulty,
            @Param("status") String status,
            @Param("topic") Topic topic,
            @Param("userId") String userId,
            Pageable pageable
    );

}
