package com.peach.ai.books;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingListRequest {
    private String number = "5";
    private String genre;
    private String subject;
    private String decade = "any";
    private String example = "";
}