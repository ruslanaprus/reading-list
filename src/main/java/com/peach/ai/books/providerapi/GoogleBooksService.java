package com.peach.ai.books.providerapi;

import com.peach.ai.books.model.BookDTO;
import com.peach.ai.books.BookDataProvider;
import com.peach.ai.books.providerapi.model.GoogleBookItem;
import com.peach.ai.books.providerapi.model.GoogleBooksResponse;
import com.peach.ai.books.providerapi.model.VolumeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

            // Use the model class directly with RestTemplate
            ResponseEntity<GoogleBooksResponse> response = restTemplate.getForEntity(url, GoogleBooksResponse.class);
            
            GoogleBooksResponse booksResponse = response.getBody();
            if (booksResponse == null || booksResponse.getItems() == null || booksResponse.getItems().isEmpty()) {
                return null;
            }

            return extractBookInfo(booksResponse.getItems().get(0));
        } catch (Exception e) {
            log.error("Error fetching book details from Google Books API", e);
            return null;
        }
    }

    private BookDTO extractBookInfo(GoogleBookItem item) {
        VolumeInfo volumeInfo = item.getVolumeInfo();

        if (volumeInfo == null) {
            return BookDTO.builder()
                    .title("Unknown Title")
                    .author("Unknown Author")
                    .pageCount(0)
                    .rating(0.0)
                    .summary("No summary available")
                    .build();
        }

        List<String> authors = volumeInfo.getAuthors();
        String author = (authors != null && !authors.isEmpty()) ? String.join(", ", authors) : "Unknown Author";

        return BookDTO.builder()
                .title(volumeInfo.getTitle() != null ? volumeInfo.getTitle() : "Unknown Title")
                .author(author)
                .pageCount(volumeInfo.getPageCount() != null ? volumeInfo.getPageCount() : 0)
                .rating(volumeInfo.getAverageRating() != null ? volumeInfo.getAverageRating() : 0.0)
                .summary(volumeInfo.getDescription() != null ? volumeInfo.getDescription() : "No summary available")
                .build();
    }

}