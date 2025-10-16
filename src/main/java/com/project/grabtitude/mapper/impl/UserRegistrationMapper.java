package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.UserRegistrationDto;
import com.project.grabtitude.entity.User;
import com.project.grabtitude.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMapper implements Mapper<User, UserRegistrationDto> {

    private ModelMapper modelMapper;
    public UserRegistrationMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    @Override
    public UserRegistrationDto mapTo(User user) {
        return modelMapper.map(user, UserRegistrationDto.class);
    }

    @Override
    public User mapFrom(UserRegistrationDto userRegistrationDto) {
        return modelMapper.map(userRegistrationDto, User.class);
    }
}
