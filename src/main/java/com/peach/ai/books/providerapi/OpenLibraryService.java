package com.peach.ai.books.providerapi;

import com.peach.ai.books.BookDTO;
import com.peach.ai.books.BookDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OpenLibraryService implements BookDataProvider {
    private final RestTemplate restTemplate;
    private static final String OPEN_LIBRARY_URL = "https://openlibrary.org/search.json?title={title}";

    public OpenLibraryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public BookDTO fetchBookData(String title, String author) {
        ResponseEntity<Map> response = restTemplate.getForEntity(OPEN_LIBRARY_URL, Map.class, title);
        List<Map<String, Object>> docs = (List<Map<String, Object>>) response.getBody().get("docs");

        if (docs == null || docs.isEmpty()) return null;

        log.info("Found {} book(s) for title: '{}'", docs.size(), title);

        return extractBookInfo(docs.get(0));
    }

    private BookDTO extractBookInfo(Map<String, Object> doc) {
        List<String> authors = (List<String>) doc.get("author_name");
        String author = (authors != null && !authors.isEmpty()) ? authors.get(0) : "Unknown";

        return BookDTO.builder()
                .title((String) doc.get("title"))
                .author(author)
                .summary("")
                .build();
    }
}
