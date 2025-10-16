package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.SubmissionResponseDto;
import com.project.grabtitude.entity.Submission;
import com.project.grabtitude.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SubmissionResponseMapper implements Mapper<Submission, SubmissionResponseDto> {
    private final ModelMapper modelMapper;
    public SubmissionResponseMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    @Override
    public SubmissionResponseDto mapTo(Submission submission) {
        return modelMapper.map(submission, SubmissionResponseDto.class);
    }

    @Override
    public Submission mapFrom(SubmissionResponseDto submissionResponseDto) {
        return modelMapper.map(submissionResponseDto, Submission.class);
    }
}
