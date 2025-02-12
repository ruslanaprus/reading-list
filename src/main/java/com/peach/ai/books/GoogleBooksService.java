package com.peach.ai.books;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GoogleBooksService {

    @Value("${books.api.key}")
    private String apiKey;
    private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q={query}&key={apiKey}";
    private final RestTemplate restTemplate;

    public GoogleBooksService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GoogleBookDTO searchBook(String title, String author){
        String query = title + "+inauthor:" + author;
        String url = GOOGLE_BOOKS_URL.replace("{query}", query).replace("{apiKey}", apiKey);

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

        if (items != null && !items.isEmpty()) {
            Map<String, Object> volumeInfo = (Map<String, Object>) items.get(0).get("volumeInfo");

            GoogleBookDTO bookDTO = new GoogleBookDTO();
            bookDTO.setTitle((String) volumeInfo.get("title"));
            bookDTO.setAuthor(author);
            bookDTO.setPageCount((Integer) volumeInfo.get("pageCount"));
            bookDTO.setGoogleRating(volumeInfo.get("averageRating") instanceof Number ?
                    ((Number) volumeInfo.get("averageRating")).doubleValue() : null);
            bookDTO.setSummary((String) volumeInfo.get("description"));

            return bookDTO;
        }
        return null;
    }

}