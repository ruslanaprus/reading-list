package com.peach.ai;

import com.peach.ai.books.BookDataProvider;
import com.peach.ai.books.providerapi.GoogleBooksService;
import com.peach.ai.books.providerapi.OpenLibraryService;
import com.peach.ai.books.providerapi.WikiBooksService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "books.provider", havingValue = "google", matchIfMissing = true)
    public BookDataProvider googleProvider(GoogleBooksService service) {
        return service;
    }

    @Bean
    @ConditionalOnProperty(name = "books.provider", havingValue = "openLibrary")
    public BookDataProvider openLibraryProvider(OpenLibraryService service) {
        return service;
    }

    @Bean
    @ConditionalOnProperty(name = "books.provider", havingValue = "wikiBooks")
    public BookDataProvider wikiBooksProvider(WikiBooksService service) {
        return service;
    }

}
