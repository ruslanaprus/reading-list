package com.peach.ai.books;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBookDTO {
    private String title;
    private String author;
    private int pageCount;
    private double googleRating;
    private String summary;
}