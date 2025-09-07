package com.project.grabtitude.controllers;

import com.project.grabtitude.dto.*;
import com.project.grabtitude.helper.AppConstants;
import com.project.grabtitude.helper.CustomPageResponse;
import com.project.grabtitude.services.ProblemService;
import com.project.grabtitude.services.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Controller", description = "APIs for managing problems and topics")
public class AdminController {
    private final ProblemService problemService;

    private final TopicService topicService;
    public AdminController(ProblemService problemService, TopicService topicService){
        this.problemService = problemService;
        this.topicService = topicService;
    }

    @Operation(
            summary = "Create a new Problem",
            description = "Adds a new problem with details like title, description, options, and answer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Problem created successfully",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/problem/create")
    public ResponseEntity<ProblemResponseDto> createProblem(@Valid @RequestBody ProblemRequestDto problemRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(problemService.createProblem(problemRequestDto));
    }

    @Operation(
            summary = "Update an existing Problem",
            description = "Updates details of an existing problem using its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problem updated successfully",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    @PutMapping("/problem/update")
    public ResponseEntity<ProblemResponseDto> updateProblem(@Valid @RequestBody ProblemUpdateDto problemUpdateDto){
        return new ResponseEntity<>(problemService.update(problemUpdateDto), HttpStatus.OK);
    }

    @Operation(
            summary = "Create a new Topic",
            description = "Adds a new topic that groups problems together."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Topic created successfully",
                    content = @Content(schema = @Schema(implementation = TopicResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("topic/create")
    public ResponseEntity<TopicResponseDto> createTopic(@Valid @RequestBody TopicRequestDto topicRequestDto){
        return new ResponseEntity<>(topicService.createTopic(topicRequestDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get Topic by ID",
            description = "Fetches topic details using the topic ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Topic retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TopicResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Topic not found")
    })
    @GetMapping("/topic/get/{id}")
    public ResponseEntity<TopicResponseDto> getTopicById(@PathVariable Long id){
        return new ResponseEntity<>(topicService.getById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Get all Topics",
            description = "Fetches a paginated list of all topics."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Topics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CustomPageResponse.class)))
    })
    @GetMapping("/topic/get-all")
    public ResponseEntity<CustomPageResponse<TopicResponseDto>> getAllTopics(@RequestParam(value = "page", defaultValue = AppConstants.page) int page,
                                                                             @RequestParam(value = "size", defaultValue = AppConstants.size) int size) {
        if(page < 0) page = Integer.parseInt(AppConstants.page);
        if(size <= 0) size = Integer.parseInt(AppConstants.size);
        return new ResponseEntity<>(topicService.getAll(page, size), HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing Topic",
            description = "Updates the details of an existing topic."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Topic updated successfully",
                    content = @Content(schema = @Schema(implementation = TopicResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Topic not found")
    })
    @PutMapping("/topic/update")
    public ResponseEntity<TopicResponseDto> updateTopic(@Valid @RequestBody TopicUpdateDto topicUpdateDto){
        return new ResponseEntity<>(topicService.updateTopic(topicUpdateDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Delete a Topic",
            description = "Deletes a topic by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Topic deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
    })
    @DeleteMapping("/topic/delete/{id}")
    public ResponseEntity<TopicResponseDto> deleteTopic(@PathVariable Long id){
        topicService.deleteTopicById(id);
        return new ResponseEntity(null, HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Delete a Problem",
            description = "Deletes a problem by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Problem deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    @DeleteMapping("/problem/delete/{id}")
    public ResponseEntity<ProblemResponseDto> deleteProblem(@PathVariable Long id){
        problemService.deleteProblemById(id);
        return new ResponseEntity(null, HttpStatus.NO_CONTENT);
    }
}
