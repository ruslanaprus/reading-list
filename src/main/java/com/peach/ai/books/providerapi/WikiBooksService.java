package com.peach.ai.books.providerapi;

import com.peach.ai.books.model.BookDTO;
import com.peach.ai.books.BookDataProvider;
import com.peach.ai.books.providerapi.model.WikiBook;
import com.peach.ai.books.providerapi.model.WikiBooksResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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
        ResponseEntity<WikiBooksResponse> response = restTemplate.getForEntity(
            WIKI_BOOKS_URL,
            WikiBooksResponse.class,
            name
        );
        
        WikiBooksResponse booksResponse = response.getBody();
        if (booksResponse == null || booksResponse.getBooks() == null || booksResponse.getBooks().isEmpty()) {
            return null;
        }

        List<WikiBook> books = booksResponse.getBooks();
        log.info("Fetched {} books for title: '{}'", books.size(), name);

        return extractBookInfo(books.get(0));
    }

    private BookDTO extractBookInfo(WikiBook book) {
        String author = book.getAuthor();
        author = (author != null && !author.isEmpty()) ? author : "Unknown";

        Integer pageCount = parsePageCount(book.getPages());

        return BookDTO.builder()
                .title(book.getName())
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
