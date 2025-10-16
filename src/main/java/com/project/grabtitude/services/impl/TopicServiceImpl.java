package com.project.grabtitude.services.impl;

import com.project.grabtitude.dto.TopicRequestDto;
import com.project.grabtitude.dto.TopicResponseDto;
import com.project.grabtitude.dto.TopicUpdateDto;
import com.project.grabtitude.entity.Category;
import com.project.grabtitude.entity.Topic;
import com.project.grabtitude.helper.CustomPageResponse;
import com.project.grabtitude.helper.ResourceNotFoundException;
import com.project.grabtitude.mapper.impl.TopicRequestMapper;
import com.project.grabtitude.mapper.impl.TopicResponseMapper;
import com.project.grabtitude.repository.CategoryRepo;
import com.project.grabtitude.repository.TopicRepo;
import com.project.grabtitude.services.TopicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TopicServiceImpl implements TopicService {
    private final TopicRepo topicRepo;
    private final TopicResponseMapper topicResponseMapper;
    private final TopicRequestMapper topicRequestMapper;
    private final CategoryRepo categoryRepo;
    public TopicServiceImpl(TopicRepo topicRepo,
                            TopicResponseMapper topicResponseMapper,
                            TopicRequestMapper topicRequestMapper,
                            CategoryRepo categoryRepo){
        this.topicRepo = topicRepo;
        this.topicResponseMapper = topicResponseMapper;
        this.topicRequestMapper = topicRequestMapper;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public TopicResponseDto getById(Long id) {
        Topic topic = topicRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with given id"));
        TopicResponseDto topicResponseDto = topicResponseMapper.mapTo(topic);
        topicResponseDto.setCategoryId(topic.getCategory().getId());
        return topicResponseDto;

    }

    @Override
    public CustomPageResponse<TopicResponseDto> getAll(int page, int size) {
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Topic> topicPage = topicRepo.findAll(pageable);

        CustomPageResponse<TopicResponseDto> responsePage = new CustomPageResponse<>();

        // this is one way and other way is use map function look down at get topic by id
        responsePage.setContent(new ArrayList<>());
        topicPage.forEach(topic -> {
            TopicResponseDto topicResponseDto = topicResponseMapper.mapTo(topic);
            topicResponseDto.setCategoryId(topic.getCategory().getId());
            responsePage.getContent().add(topicResponseDto);
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
        Category category = categoryRepo.findById(topicUpdateDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id : " + topicUpdateDto.getCategoryId()));

        Topic savedTopic = topicRepo.findById(topicUpdateDto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id : " + topicUpdateDto.getId()));
        savedTopic.setName(topicUpdateDto.getName());
        savedTopic.setDescription(topicUpdateDto.getDescription());
        savedTopic.setCategory(category);
        Topic updatedTopic = topicRepo.save(savedTopic);
        TopicResponseDto topicResponseDto = topicResponseMapper.mapTo(updatedTopic);
        topicResponseDto.setCategoryId(category.getId());
        return topicResponseDto;
    }

    @Override
    public void deleteTopicById(long id) {
        topicRepo.deleteById(id);
        return;
    }

    @Override
    public TopicResponseDto createTopic(TopicRequestDto topicRequestDto) {
        Category category = categoryRepo.findById(topicRequestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id : " + topicRequestDto.getCategoryId()));
        Topic topic = topicRequestMapper.mapFrom(topicRequestDto);
        topic.setCategory(category);
        Topic savedTopic = topicRepo.save(topic);
        TopicResponseDto topicResponseDto = topicResponseMapper.mapTo(savedTopic);
        topicResponseDto.setCategoryId(topic.getCategory().getId());
        return topicResponseDto;
    }

    @Override
    public CustomPageResponse<TopicResponseDto> getByCategory(Long id, int page, int size) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id : " + id));
        Sort sort = Sort.by("id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Topic> topicPage = topicRepo.findByCategory(category, pageable);
        Page<TopicResponseDto> topicResponsePage = topicPage.map(topic -> {
            TopicResponseDto topicResponseDto = topicResponseMapper.mapTo(topic);
            topicResponseDto.setCategoryId(category.getId());
            return topicResponseDto;
        });

        CustomPageResponse<TopicResponseDto> responsePage = new CustomPageResponse<>();

        responsePage.setContent(topicResponsePage.getContent());
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
    public Long getTotalTopics() {
        return topicRepo.count();
    }
}
