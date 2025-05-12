package com.peach.ai.books.queue.messageDTO;

import com.peach.ai.books.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadingListResponse {
    private String requestId;
    private List<Book> books;
}