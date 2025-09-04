package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.PendingVerificationUserResponseDto;
import com.project.grabtitude.entity.PendingVerificationUser;
import com.project.grabtitude.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PendingVerificationUserResponseMapper implements Mapper<PendingVerificationUser, PendingVerificationUserResponseDto> {
    private final ModelMapper modelMapper;
    public PendingVerificationUserResponseMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    @Override
    public PendingVerificationUserResponseDto mapTo(PendingVerificationUser pendingVerificationUser) {
        return modelMapper.map(pendingVerificationUser, PendingVerificationUserResponseDto.class);
    }

    @Override
    public PendingVerificationUser mapFrom(PendingVerificationUserResponseDto pendingVerificationUserResponseDto) {
        return modelMapper.map(pendingVerificationUserResponseDto, PendingVerificationUser.class);
    }
}
