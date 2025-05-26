package com.peach.ai.books.providerapi.model;

import java.util.List;
import lombok.Data;

@Data
public class WikiBooksResponse {
    private List<WikiBook> books;
}
