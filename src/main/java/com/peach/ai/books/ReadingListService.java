package com.peach.ai.books;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReadingListService {
    private final ChatModel chatModel;
    private final GoogleBooksService googleBooksService;
    private final ObjectMapper objectMapper;

    public ReadingListService(ChatModel chatModel, GoogleBooksService googleBooksService, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.googleBooksService = googleBooksService;
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

                Return a JSON array of book objects, each with:
                - "title": (string)
                - "author": (string)
                - "pages": (integer, optional)
                - "summary": (string)

                Do not hallucinate.
                Strictly return only JSON. No explanations, no extra text.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Map<String, Object> params = Map.of(
                "number", number,
                "genre", genre,
                "subject", subject,
                "decade", decade,
                "example", example
        );

        Prompt prompt = promptTemplate.create(params);
        String aiResponse = chatModel.call(prompt).getResult().getOutput().getContent();
        // handle non-JSON responses
        if (!aiResponse.startsWith("[")) {
            throw new RuntimeException("Invalid AI response: Expected JSON array but got: " + aiResponse);
        }

        // parse JSON response from AI
        List<Book> books;
        try {
            books = objectMapper.readValue(aiResponse, new TypeReference<List<Book>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }

        // fetch additional book data from Google Books API
        books.forEach(book -> {
            GoogleBookDTO bookData = googleBooksService.searchBook(book.getTitle(), book.getAuthor());
            if (bookData != null) {
                book.setSummary(bookData.getSummary() !=null ? bookData.getSummary() : book.getTitle());
                book.setPages(bookData.getPageCount() != null ? bookData.getPageCount() : book.getPages());
                book.setGoogleRating(bookData.getGoogleRating());
            }

        });

        return books;
    }

}
