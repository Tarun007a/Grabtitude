package com.project.grabtitude.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;

    private String title;

    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    long totalSubmissions = 0;
    long correctSubmissions = 0;

    public enum Difficulty{
        EASY, MEDIUM, HARD, EXPERT
    }

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    // for topic we controlled the name and other things like nullable and all but by default
    // if will create a fk as above and also it will be nullable = false and fetch type lazy is default
    // as in eager hibernate will get the category at the same time we get problem with using a extra
    // query/join or computation this is good when we need category at same time like it will be always
    // used but in our case it is not so we use lazy, in lazy as we use category it will be fetched at
    // that time only
    //both ways will ultimately create the same relationship in the database

    @ManyToOne
    private Category category;

    private String explanation;

}
