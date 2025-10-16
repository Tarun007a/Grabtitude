package com.project.grabtitude.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicUpdateDto {
    @NotNull(message = "Please enter topic id for topic to be updated, topic id is returned when creating a topic")
    private Long id;

    @NotNull(message = "Please enter updated name of topic")
    @Size(min = 2, max = 100, message = "Topic name can be minimum 2 character and maximum 100 character")
    private String name;

    @NotNull(message = "Please enter updated description of topic")
    @Size(min = 2, max = 1000, message = "Topic description can be minimum 2 character and maximum 1000 character")
    private String description;

    @NotNull(message = "Please enter the category id for topic")
    private Long categoryId;
}
