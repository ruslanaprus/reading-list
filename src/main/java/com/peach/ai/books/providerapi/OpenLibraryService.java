package com.peach.ai.books.providerapi;

import com.peach.ai.books.model.BookDTO;
import com.peach.ai.books.BookDataProvider;
import com.peach.ai.books.providerapi.model.OpenLibraryDoc;
import com.peach.ai.books.providerapi.model.OpenLibraryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
        ResponseEntity<OpenLibraryResponse> response = restTemplate.getForEntity(
            OPEN_LIBRARY_URL, 
            OpenLibraryResponse.class, 
            title
        );
        
        OpenLibraryResponse libraryResponse = response.getBody();
        if (libraryResponse == null || libraryResponse.getDocs() == null || libraryResponse.getDocs().isEmpty()) {
            return null;
        }

        List<OpenLibraryDoc> docs = libraryResponse.getDocs();
        log.info("Found {} book(s) for title: '{}'", docs.size(), title);

        return extractBookInfo(docs.get(0));
    }

    private BookDTO extractBookInfo(OpenLibraryDoc doc) {
        List<String> authors = doc.getAuthor_name();
        String author = (authors != null && !authors.isEmpty()) ? authors.get(0) : "Unknown";

        return BookDTO.builder()
                .title(doc.getTitle())
                .author(author)
                .summary("")
                .build();
    }
}
