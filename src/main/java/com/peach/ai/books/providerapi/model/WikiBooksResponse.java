package com.peach.ai.books.providerapi.model;

import java.util.List;

public class WikiBooksResponse {
    private List<WikiBook> books;

    public List<WikiBook> getBooks() {
        return books;
    }

    public void setBooks(List<WikiBook> books) {
        this.books = books;
    }
}
