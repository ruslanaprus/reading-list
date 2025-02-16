package com.peach.ai.books;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReadingListService {
    private final ChatModel chatModel;
    private final BookDataProvider bookDataProvider;
    private final ObjectMapper objectMapper;

    public ReadingListService(ChatModel chatModel,
                              ApplicationContext context,
                              @Value("${books.provider}") String bookProvider,
                              ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.bookDataProvider = context.getBean(bookProvider + "Service", BookDataProvider.class);
        this.objectMapper = objectMapper;
    }

    public List<Book> createReadingList(String number,
                                        String genre,
                                        String subject,
                                        String decade,
                                        String example) {

        var template = """
                I want to create a personalized reading list of **{number}** books in the **{genre}** genre.
                I am interested in books about **{subject}**.

                - If possible, prioritize books written during **{decade}**. (If not provided, ignore this criterion.)
                - If an example book is given (**{example}**), please consider its style, themes, or writing approach when making recommendations.

                **Important:**
                - Do not hallucinate. Only return books with **verified authors** who are widely recognized.
                - If unsure, return "author": "Unknown" instead of making up a name.
                - Use reliable sources such as bestseller lists, literary awards, or books available in major libraries.
                
                Strictly return only JSON. No extra text apart from JSON.
                Return a JSON array of book objects, each with:
                - "title": (string)
                - "author": (string)
                - "summary": (string)
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Map<String, Object> params = Map.of(
                "number", number,
                "genre", genre,
                "subject", subject,
                "decade", decade,
                "example", example
        );

        Prompt prompt = promptTemplate.create(params,
                ChatOptions.builder()
                        .temperature(0.0)
                        .build());

        log.info(prompt.toString());

        String aiResponse = chatModel.call(prompt).getResult().getOutput().getContent();

        log.info("Response from AI: {}", aiResponse);

        // handle non-JSON responses
        if (!aiResponse.startsWith("[")) {
            log.error("Invalid AI response: {}", aiResponse);
            throw new RuntimeException("Invalid AI response: Expected JSON array");
        }

        List<Book> books = parseAiResponse(aiResponse);
        books.forEach(this::enrichWithBookData);

        return books;
    }

    // parse JSON response from AI
    private List<Book> parseAiResponse(String response) {
        try {
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Failed to parse AI response", e);
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    // fetch additional book data from Books API
    private void enrichWithBookData(Book book) {
        BookDTO bookData = bookDataProvider.fetchBookData(book.getTitle(), book.getAuthor());
        if (bookData != null) {
            if (bookData.getAuthor() != null && !bookData.getAuthor().isEmpty()) {
                book.setAuthor(bookData.getAuthor());
            }

            if (bookData.getSummary() != null && !bookData.getSummary().isEmpty()) {
                book.setSummary(bookData.getSummary());
            } else {
                // keep AI-generated summary if books api doesn't provide one
                if (book.getSummary() != null && !book.getSummary().isEmpty()) {
                    book.setSummary(book.getSummary());
                }
            }

            book.setPages(bookData.getPageCount() != 0 ? bookData.getPageCount() : book.getPages());
            book.setRating(bookData.getRating());
        }
    }

}
