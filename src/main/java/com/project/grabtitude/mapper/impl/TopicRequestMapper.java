package com.project.grabtitude.mapper.impl;

import com.project.grabtitude.dto.TopicRequestDto;
import com.project.grabtitude.entity.Topic;
import com.project.grabtitude.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TopicRequestMapper implements Mapper<Topic, TopicRequestDto> {
    private final ModelMapper modelMapper;
    public TopicRequestMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    @Override
    public TopicRequestDto mapTo(Topic topic) {
        return modelMapper.map(topic, TopicRequestDto.class);
    }

    @Override
    public Topic mapFrom(TopicRequestDto topicRequestDto) {
        return modelMapper.map(topicRequestDto, Topic.class);
    }
}
