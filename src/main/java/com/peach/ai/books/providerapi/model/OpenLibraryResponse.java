package com.peach.ai.books.providerapi.model;

import java.util.List;

public class OpenLibraryResponse {
    private List<OpenLibraryDoc> docs;

    public List<OpenLibraryDoc> getDocs() {
        return docs;
    }

    public void setDocs(List<OpenLibraryDoc> docs) {
        this.docs = docs;
    }
}
