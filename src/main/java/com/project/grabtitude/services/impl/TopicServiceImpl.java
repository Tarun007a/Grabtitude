package com.project.grabtitude.services.impl;

import com.project.grabtitude.dto.TopicRequestDto;
import com.project.grabtitude.dto.TopicResponseDto;
import com.project.grabtitude.dto.TopicUpdateDto;
import com.project.grabtitude.entity.Topic;
import com.project.grabtitude.helper.CustomPageResponse;
import com.project.grabtitude.helper.ResourceNotFoundException;
import com.project.grabtitude.mapper.impl.TopicRequestMapper;
import com.project.grabtitude.mapper.impl.TopicResponseMapper;
import com.project.grabtitude.repository.TopicRepo;
import com.project.grabtitude.services.TopicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class TopicServiceImpl implements TopicService {
    private final TopicRepo topicRepo;
    private final TopicResponseMapper topicResponseMapper;
    private final TopicRequestMapper topicRequestMapper;
    public TopicServiceImpl(TopicRepo topicRepo,
                            TopicResponseMapper topicResponseMapper,
                            TopicRequestMapper topicRequestMapper){
        this.topicRepo = topicRepo;
        this.topicResponseMapper = topicResponseMapper;
        this.topicRequestMapper = topicRequestMapper;
    }

    @Override
    public TopicResponseDto getById(Long id) {
        Optional<Topic> topicOptional = topicRepo.findById(id);
        if(topicOptional.isEmpty()) throw new ResourceNotFoundException("Topic not found with given id");
        return topicResponseMapper.mapTo(topicOptional.get());
    }

    @Override
    public CustomPageResponse<TopicResponseDto> getAll(int page, int size) {
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Topic> topicPage = topicRepo.findAll(pageable);

        CustomPageResponse<TopicResponseDto> responsePage = new CustomPageResponse<>();

        responsePage.setContent(new ArrayList<>());
        topicPage.forEach(topic -> {
           responsePage.getContent().add(topicResponseMapper.mapTo(topic));
        });

        responsePage.setPageNumber(topicPage.getNumber());
        responsePage.setPageSize(topicPage.getSize());
        responsePage.setLast(topicPage.isLast());
        responsePage.setFirst(topicPage.isFirst());
        responsePage.setTotalPages(topicPage.getTotalPages());
        responsePage.setTotalNumberOfElements(topicPage.getTotalElements());
        responsePage.setNumberOfElements(topicPage.getNumberOfElements());

        return responsePage;
    }

    @Override
    public TopicResponseDto updateTopic(TopicUpdateDto topicUpdateDto) {
        Optional<Topic> topicOptional = topicRepo.findById(topicUpdateDto.getId());
        if(topicOptional.isEmpty()) throw new ResourceNotFoundException("Topic does not exist with id : " + topicUpdateDto.getId());
        Topic savedTopic = topicOptional.get();
        savedTopic.setName(topicUpdateDto.getName());
        savedTopic.setDescription(topicUpdateDto.getDescription());
        Topic updatedTopic = topicRepo.save(savedTopic);
        return topicResponseMapper.mapTo(updatedTopic);
    }

    @Override
    public void deleteTopicById(long id) {
        topicRepo.deleteById(id);
        return;
    }

    @Override
    public TopicResponseDto createTopic(TopicRequestDto topicRequestDto) {
        Topic topic = topicRequestMapper.mapFrom(topicRequestDto);
        Topic savedTopic = topicRepo.save(topic);
        return topicResponseMapper.mapTo(savedTopic);
    }
}
