package com.project.grabtitude.repository;

import com.project.grabtitude.entity.Category;
import com.project.grabtitude.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepo extends JpaRepository<Topic, Long> {

    Page<Topic> findAll(Pageable pageable);

    Page<Topic> findByCategory(Category category, Pageable pageable);
}
