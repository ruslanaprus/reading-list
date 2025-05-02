package com.peach.ai.books.providerapi;

import com.peach.ai.books.model.BookDTO;
import com.peach.ai.books.BookDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GoogleBooksService implements BookDataProvider {

    @Value("${books.api.key}")
    private String apiKey;
    private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q={query}&key={apiKey}";
    private final RestTemplate restTemplate;

    public GoogleBooksService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public BookDTO fetchBookData(String title, String author){
        try{
            String query = "intitle:\"" + title + "\"";//+inauthor:" + author;
            String url = GOOGLE_BOOKS_URL.replace("{query}", query).replace("{apiKey}", apiKey);

            String sanitizedUrl = url.replace(apiKey, "*****");
            log.info("Searching for Google Books in {}", sanitizedUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

            if (items == null || items.isEmpty()) return null;

            return extractBookInfo(items.get(0));
        } catch (Exception e) {
            log.error("Error fetching book details from Google Books API", e);
            return null;
        }
    }

    private BookDTO extractBookInfo(Map<String, Object> item) {
        Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");

        if (volumeInfo == null) {
            return BookDTO.builder()
                    .title("Unknown Title")
                    .author("Unknown Author")
                    .pageCount(0)
                    .rating(0.0)
                    .summary("No summary available")
                    .build();
        }

        List<String> authors = (List<String>) volumeInfo.get("authors");
        String author = (authors != null && !authors.isEmpty()) ? String.join(", ", authors) : "Unknown Author";

        return BookDTO.builder()
                .title((String) volumeInfo.get("title"))
                .author(author)
                .pageCount(((Number) volumeInfo.getOrDefault("pageCount", 0)).intValue())
                .rating(((Number) volumeInfo.getOrDefault("averageRating", 0.0)).doubleValue())
                .summary((String) volumeInfo.getOrDefault("description", "No summary available"))
                .build();
    }

}