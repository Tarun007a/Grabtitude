package com.project.grabtitude.services.impl;

import com.project.grabtitude.repository.SubmissionRepo;
import com.project.grabtitude.services.SubmissionService;
import org.springframework.stereotype.Service;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepo submissionRepo;

    public SubmissionServiceImpl(SubmissionRepo submissionRepo){
        this.submissionRepo = submissionRepo;
    }
    @Override
    public Long getTotalSubmissions() {
        return submissionRepo.count();
    }
}
