package com.peach.ai.books;

public interface BookDataProvider {
    BookDTO fetchBookData(String title, String author);
}
