package com.peach.ai.books.providerapi.model;

import java.util.List;

public class GoogleBooksResponse {
    private List<GoogleBookItem> items;

    public List<GoogleBookItem> getItems() {
        return items;
    }

    public void setItems(List<GoogleBookItem> items) {
        this.items = items;
    }
}
