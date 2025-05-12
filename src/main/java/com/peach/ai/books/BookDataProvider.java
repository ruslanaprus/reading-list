package com.peach.ai.books;

import com.peach.ai.books.model.BookDTO;

public interface BookDataProvider {
    BookDTO fetchBookData(String title, String author);
}
