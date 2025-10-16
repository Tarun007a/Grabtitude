package com.project.grabtitude.controllers;


import com.project.grabtitude.dto.FilterDto;
import com.project.grabtitude.dto.ProblemRequestDto;
import com.project.grabtitude.dto.ProblemResponseDto;
import com.project.grabtitude.dto.ProblemUpdateDto;
import com.project.grabtitude.helper.AppConstants;
import com.project.grabtitude.helper.CustomPageResponse;
import com.project.grabtitude.services.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/problems")
@Tag(name = "Problem Controller", description = "APIs for retrieving and searching coding problems")
public class ProblemController {
    private final ProblemService problemService;
    public ProblemController(ProblemService problemService){
        this.problemService = problemService;
    }

    @Operation(
            summary = "Get All Problems (Paginated)",
            description = "Retrieves a paginated list of all coding problems available in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problems retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CustomPageResponse.class)))
    })
    // GET /problems → to fetch all problems
    @GetMapping
    public ResponseEntity<CustomPageResponse<ProblemResponseDto>> getAllProblems(
            @RequestParam(value = "page", defaultValue = AppConstants.page) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.size) int size){
        if(size > 30) size = 30;    // increase efficiency
        return new ResponseEntity<>(problemService.getProblems(page, size), HttpStatus.OK);
    }

    @Operation(
            summary = "Get Problem by ID",
            description = "Fetches a specific problem using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problem retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    // GET /problems/{id} → to fetch problem by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponseDto> getProblem(@PathVariable Long id){
        return new ResponseEntity<>(problemService.getById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Create a New Problem",
            description = "Creates a new coding problem in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Problem created successfully",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    // POST /problems → to create a new problem (public endpoint for creating problems)
    @PostMapping
    public ResponseEntity<ProblemResponseDto> createProblem(@Valid @RequestBody ProblemRequestDto problemRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(problemService.createProblem(problemRequestDto));
    }

    @Operation(
            summary = "Update Problem",
            description = "Updates an existing problem by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problem updated successfully",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    // PUT /problems/{id} → to update an existing problem
    @PutMapping("/{id}")
    public ResponseEntity<ProblemResponseDto> updateProblem(@PathVariable Long id, @Valid @RequestBody ProblemUpdateDto problemUpdateDto){
        // Set the problem ID from path parameter
        problemUpdateDto.setProblemId(id);
        return new ResponseEntity<>(problemService.update(problemUpdateDto), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete Problem",
            description = "Deletes a problem by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Problem deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    // DELETE /problems/{id} → to delete a problem
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id){
        problemService.deleteProblemById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Legacy endpoints for backward compatibility
    @Operation(
            summary = "Get Problem by ID (Legacy)",
            description = "Fetches a specific problem using its unique ID. Legacy endpoint."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problem retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    @GetMapping("/get/{id}")
    public ProblemResponseDto getProblemLegacy(@PathVariable Long id){
        return problemService.getById(id);
    }

    @Operation(
            summary = "Get All Problems (Paginated)",
            description = "Retrieves a paginated list of all coding problems available in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problems retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CustomPageResponse.class)))
    })
    @GetMapping("/get-problems")
    public ResponseEntity<CustomPageResponse<ProblemResponseDto>> getProblemsLegacy(
            @RequestParam(value = "page", defaultValue = AppConstants.page) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.size) int size){
        if(size > 30) size = 30;    // increase efficiency
        return new ResponseEntity<>(problemService.getProblems(page, size), HttpStatus.OK);
    }

    @Operation(
            summary = "Search Problems",
            description = "Searches problems by keyword. Results are paginated."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problems matching keyword retrieved",
                    content = @Content(schema = @Schema(implementation = CustomPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping("/search")
    public ResponseEntity<CustomPageResponse<ProblemResponseDto>> searchProblem(
            @RequestParam(value = "page", defaultValue = AppConstants.page) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.size) int size,
            @RequestParam(value = "keyword", defaultValue = AppConstants.keyword) String keyword){
        if(size > 30) size = 30;    // increase efficiency
        return new ResponseEntity(problemService.search(keyword, page, size), HttpStatus.OK);
    }

    @GetMapping("/get-by-difficulty")
    public ResponseEntity<CustomPageResponse<ProblemResponseDto>> getProblemsByDifficulty(
            @RequestParam(value = "page", defaultValue = AppConstants.page) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.size) int size,
            @RequestParam(value = "difficulty", defaultValue = AppConstants.keyword) String difficulty){
        if(size > 30) size = 30;    // increase efficiency
        return new ResponseEntity(problemService.searchByDifficulty(difficulty, page, size), HttpStatus.OK);
    }

    @GetMapping("/get-by-topic")
    public ResponseEntity<CustomPageResponse<ProblemResponseDto>> getProblemsByTopic(
            @RequestParam(value = "page", defaultValue = AppConstants.page) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.size) int size,
            @RequestParam(value = "topic", defaultValue = AppConstants.keyword) Long topicId){
        if(size > 30) size = 30;    // increase efficiency
        return new ResponseEntity(problemService.searchByTopic(topicId, page, size), HttpStatus.OK);
    }

    @GetMapping("/get-next-problem")
    public ResponseEntity<ProblemResponseDto> getNextProblem(
            @RequestParam(value = "curr-problem", defaultValue = AppConstants.defaultProblemId) Long id
    ){
        return new ResponseEntity<>(problemService.getNextForProblem(id), HttpStatus.OK);
    }

    @GetMapping("/get-previous-problem")
    public ResponseEntity<ProblemResponseDto> getPreviousProblem(
            @RequestParam(value = "curr-problem", defaultValue = AppConstants.defaultProblemId) Long id
    ){
        return new ResponseEntity<>(problemService.getPreviousForProblem(id), HttpStatus.OK);
    }

    @GetMapping("/get-total-problems")
    public ResponseEntity<Long> getTotalProblems(){
        return new ResponseEntity<>(problemService.getTotalProblems(), HttpStatus.OK);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<CustomPageResponse<ProblemResponseDto>> getProblemsByCategory(
            @PathVariable(value = "id") Long id,
            @RequestParam(value = "page", defaultValue = AppConstants.page) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.size) int size){
        if(size > 30) size = 30;
        return new ResponseEntity<>(problemService.getByCategory(id, page, size), HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<CustomPageResponse<ProblemResponseDto>> getProblemsByFilter(
            @RequestParam(value = "page", defaultValue = AppConstants.page) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.size) int size,
            @Valid @RequestBody FilterDto filterDto
            ){
        if(size > 30) size = 30;
        return ResponseEntity.ok().body(problemService.filter(filterDto , page, size));
    }
}
