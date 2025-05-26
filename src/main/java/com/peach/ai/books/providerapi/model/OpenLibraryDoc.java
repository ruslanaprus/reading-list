package com.peach.ai.books.providerapi.model;

import java.util.List;
import lombok.Data;

@Data
public class OpenLibraryDoc {
    private String title;
    private List<String> author_name;
}
