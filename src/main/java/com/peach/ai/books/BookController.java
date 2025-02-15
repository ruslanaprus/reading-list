package com.peach.ai.books;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final ReadingListService readingListService;

    public BookController(ReadingListService readingListService) {
        this.readingListService = readingListService;
    }

    @PostMapping("reading-list")
    public ResponseEntity<List<Book>> readingListCreator(@RequestBody ReadingListRequest request) {
        return ResponseEntity.ok(readingListService.createReadingList(
                request.getNumber(), request.getGenre(), request.getSubject(), request.getDecade(), request.getExample()
        ));
    }

}