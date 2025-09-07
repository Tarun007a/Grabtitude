package com.project.grabtitude.controllers;


import com.project.grabtitude.dto.ProblemResponseDto;
import com.project.grabtitude.helper.AppConstants;
import com.project.grabtitude.helper.CustomPageResponse;
import com.project.grabtitude.services.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
            summary = "Get Problem by ID",
            description = "Fetches a specific problem using its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Problem retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    @GetMapping("/get/{id}")
    public ProblemResponseDto getProblem(@PathVariable Long id){
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
    public ResponseEntity<CustomPageResponse<ProblemResponseDto>> getProblems(
            @RequestParam(value = "page", defaultValue = AppConstants.page) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.size) int size){
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
        return new ResponseEntity(problemService.search(keyword, page, size), HttpStatus.OK);
    }

}
