package com.peach.ai.books;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String title;
    private String author;
    private int pages;
    private double rating;
    private String summary;
}