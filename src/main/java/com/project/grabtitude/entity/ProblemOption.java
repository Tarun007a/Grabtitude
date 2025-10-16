package com.project.grabtitude.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProblemOption {
    //entity for a single option and each problem can have multiple options so we have many to one
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //text in every option
    @Column(nullable = false)
    private String content;

    //weather the option is correct or not
    @Column(nullable = false)
    private Boolean correct;

    //joining by the problem id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
}
