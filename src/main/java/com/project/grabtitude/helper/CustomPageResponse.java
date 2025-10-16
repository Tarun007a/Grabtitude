package com.project.grabtitude.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomPageResponse<T> {
    int pageSize;
    int pageNumber;
    int numberOfElements;         // elements in current page
    int totalPages;               // total number of pages
    Long totalNumberOfElements;    //total elements in db
    List<T> content;
    boolean isLast;
    boolean isFirst;
}
