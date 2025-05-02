package com.peach.ai.books.providerapi;

import com.peach.ai.books.model.BookDTO;
import com.peach.ai.books.BookDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class WikiBooksService implements BookDataProvider {
    private final RestTemplate restTemplate;
    private static final String WIKI_BOOKS_URL = "https://book1.slpixe.com/search?name={name}&language=english";

    public WikiBooksService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public BookDTO fetchBookData(String name, String author) {
        ResponseEntity<Map> response = restTemplate.getForEntity(WIKI_BOOKS_URL, Map.class, name);
        List<Map<String, Object>> books = (List<Map<String, Object>>) response.getBody().get("books");

        if (books == null || books.isEmpty()) return null;

        log.info("Fetched {} books for title: '{}'", books.size(), name);

        return extractBookInfo(books.get(0));
    }

    private BookDTO extractBookInfo(Map<String, Object> book) {
        String author = (String) book.get("author");
        author = (author != null || author.isEmpty()) ? author : "Unknown";

        Integer pageCount = parsePageCount(book.get("pages"));

        return BookDTO.builder()
                .title((String) book.get("name"))
                .author(author)
                .pageCount(pageCount)
                .summary("")
                .build();
    }

    private Integer parsePageCount(Object pages) {
        if (pages instanceof Integer) {
            return (Integer) pages;
        } else if (pages instanceof String) {
            Matcher matcher = Pattern.compile("\\d+").matcher((String) pages);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group());
            }
        }
        return 0;
    }

}
