package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.PendingVerificationUserRequestDto;
import com.project.grabtitude.entity.PendingVerificationUser;
import com.project.grabtitude.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PendingVerificationUserRequestMapper implements Mapper<PendingVerificationUser, PendingVerificationUserRequestDto> {
    private final ModelMapper modelMapper;
    public PendingVerificationUserRequestMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    @Override
    public PendingVerificationUserRequestDto mapTo(PendingVerificationUser pendingVerificationUser) {
        return modelMapper.map(pendingVerificationUser, PendingVerificationUserRequestDto.class);
    }

    @Override
    public PendingVerificationUser mapFrom(PendingVerificationUserRequestDto pendingVerificationUserRequestDto) {
        return modelMapper.map(pendingVerificationUserRequestDto, PendingVerificationUser.class);
    }
}
