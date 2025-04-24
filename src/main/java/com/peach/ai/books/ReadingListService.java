package com.peach.ai.books;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReadingListService {
    private final VertexAiGeminiChatModel chatModel;
    private final BookDataProvider bookDataProvider;

    public ReadingListService(VertexAiGeminiChatModel chatModel,
                              ApplicationContext context,
                              @Value("${books.provider}") String bookProvider) {
        this.chatModel = chatModel;
        this.bookDataProvider = context.getBean(bookProvider + "Service", BookDataProvider.class);
    }

    public List<Book> createReadingList(String number,
                                        String genre,
                                        String subject,
                                        String decade,
                                        String example) {

        BeanOutputConverter<List<Book>> outputConverter =
                new BeanOutputConverter<>(new ParameterizedTypeReference<>() {});

        String format = outputConverter.getFormat();

        var template = """
                I want to create a personalized reading list of **{number}** books in the **{genre}** genre.
                I am interested in books about **{subject}**.

                - Only include books that match **both** the requested genre (**{genre}**) and subject (**{subject}**).
                - If possible, prioritize books written during **{decade}**. (If not provided, ignore this criterion.)
                - If an example book is given (**{example}**), please consider its style, themes, or writing approach when making recommendations.

                ### **Important Rules:**
                1. **Only return books with verified authors** who have published works listed in major literary databases (e.g., Goodreads, Amazon, Library of Congress, or national library catalogs). \s
                3. **Books must match both the requested genre and subject.** If no books fit both criteria, return an empty list instead of unrelated recommendations. \s
                4. **Do not generate fictional or speculative book titles.** Every book must be a real, published work. \s
                5. **Use only reliable sources** such as bestseller lists, literary awards, or books widely available in major libraries. \s
                6. **If unsure about a book’s authenticity, do not include it in the list.** If no books meet the criteria, return an empty list instead of speculative results. \s
                
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Map<String, Object> params = Map.of(
                "number", number,
                "genre", genre,
                "subject", subject,
                "decade", decade,
                "example", example,
                "format", format
        );

        Prompt prompt = promptTemplate.create(params,
                VertexAiGeminiChatOptions.builder()
                        .temperature(0.0)
                        .build());

        log.info(prompt.toString());

        String aiResponse = chatModel.call(prompt).getResult().getOutput().getContent();

        log.info("Response from AI: {}", aiResponse);

        List<Book> books = outputConverter.convert(aiResponse);

        books.forEach(this::enrichWithBookData);

        return books;
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
