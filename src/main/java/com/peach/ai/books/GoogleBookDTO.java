package com.peach.ai.books;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBookDTO {
    private String title;
    private String author;
    private Integer pageCount;
    private String summary;
}