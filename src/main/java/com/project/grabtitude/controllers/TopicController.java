package com.project.grabtitude.controllers;

import com.project.grabtitude.dto.ProblemResponseDto;
import com.project.grabtitude.dto.TopicRequestDto;
import com.project.grabtitude.dto.TopicResponseDto;
import com.project.grabtitude.dto.TopicUpdateDto;
import com.project.grabtitude.helper.CustomPageResponse;
import com.project.grabtitude.services.TopicService;
import com.project.grabtitude.helper.AppConstants;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topics")
public class TopicController {
    
    private final TopicService topicService;
    
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    // GET /topics - Get all topics with pagination (public access)
    @GetMapping
    public ResponseEntity<CustomPageResponse<TopicResponseDto>> getAllTopics(
            @RequestParam(value = "page", defaultValue = AppConstants.page) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.size) int size) {
        if(page < 0) page = Integer.parseInt(AppConstants.page);
        if(size <= 0) size = Integer.parseInt(AppConstants.size);
        return new ResponseEntity<>(topicService.getAll(page, size), HttpStatus.OK);
    }

    // GET /topics/{id} - Get topic by ID (public access)
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDto> getTopicById(@PathVariable Long id) {
        return new ResponseEntity<>(topicService.getById(id), HttpStatus.OK);
    }

    // POST /topics - Create new topic (admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TopicResponseDto> createTopic(@Valid @RequestBody TopicRequestDto topicRequestDto) {
        return new ResponseEntity<>(topicService.createTopic(topicRequestDto), HttpStatus.CREATED);
    }

    // PUT /topics/{id} - Update topic (admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TopicResponseDto> updateTopic(
            @PathVariable Long id, 
            @Valid @RequestBody TopicRequestDto topicRequestDto) {
        // Create TopicUpdateDto with the ID and request data
        TopicUpdateDto topicUpdateDto = new TopicUpdateDto();
        topicUpdateDto.setId(id);
        topicUpdateDto.setName(topicRequestDto.getName());
        topicUpdateDto.setDescription(topicRequestDto.getDescription());
        
        return new ResponseEntity<>(topicService.updateTopic(topicUpdateDto), HttpStatus.OK);
    }

    // DELETE /topics/{id} - Delete topic (admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopicById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<CustomPageResponse<TopicResponseDto>> getTopicByCategory(@PathVariable Long id,
                                      @RequestParam(value = "page", defaultValue = AppConstants.page) int page,
                                      @RequestParam(value = "size", defaultValue = AppConstants.size) int size
    ){
        return ResponseEntity.ok().body(topicService.getByCategory(id, page, size));
    }
}
