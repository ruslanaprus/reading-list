package com.peach.ai.books;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDTO {
    private String title;
    private String author;
    private int pageCount;
    private double rating;
    private String summary;
}