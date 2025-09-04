package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.TopicResponseDto;
import com.project.grabtitude.entity.Topic;
import com.project.grabtitude.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TopicResponseMapper implements Mapper<Topic, TopicResponseDto> {
    private final ModelMapper modelMapper;
    public TopicResponseMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public TopicResponseDto mapTo(Topic topic) {
        return modelMapper.map(topic, TopicResponseDto.class);
    }

    @Override
    public Topic mapFrom(TopicResponseDto topicResponseDto) {
        return modelMapper.map(topicResponseDto, Topic.class);
    }
}
