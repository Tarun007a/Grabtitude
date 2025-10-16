package com.project.grabtitude.services;


import com.project.grabtitude.dto.TopicRequestDto;
import com.project.grabtitude.dto.TopicResponseDto;
import com.project.grabtitude.dto.TopicUpdateDto;
import com.project.grabtitude.helper.CustomPageResponse;

public interface TopicService {
    TopicResponseDto createTopic(TopicRequestDto topicRequestDto);

    TopicResponseDto getById(Long id);

    CustomPageResponse<TopicResponseDto> getAll(int page, int size);

    TopicResponseDto updateTopic(TopicUpdateDto topicUpdateDto);

    void deleteTopicById(long id);

    CustomPageResponse<TopicResponseDto> getByCategory(Long id, int page, int size);

    Long getTotalTopics();
}
