package com.peach.ai.books;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Book {
    private String title;
    private String author;
    private Integer pages;
    private Double googleRating;
    private String summary;
}