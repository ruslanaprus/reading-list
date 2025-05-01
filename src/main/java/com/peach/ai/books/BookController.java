package com.peach.ai.books;

import com.peach.ai.queue.RabbitMQConfig;
import com.peach.ai.queue.messageDTO.ReadingListMessage;
import com.peach.ai.queue.repository.ReadingListRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final RabbitTemplate rabbitTemplate;
    private final ReadingListRepository readingListRepository;

    public BookController(RabbitTemplate rabbitTemplate, ReadingListRepository readingListRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.readingListRepository = readingListRepository;
    }

    @PostMapping("reading-list")
    public ResponseEntity<String> readingListCreator(@RequestBody ReadingListRequest request) {
        String requestId = UUID.randomUUID().toString();
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, new ReadingListMessage(requestId, request));

        return ResponseEntity.accepted().body("Request received. Use requestId: " + requestId + " to retrieve the results.");
    }

    @GetMapping("reading-list/{requestId}")
    public ResponseEntity<?> getReadingList(@PathVariable String requestId) {
        List<Book> books = readingListRepository.getResponse(requestId);

        if (books == null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Reading list is still being generated. Please try again later.");
        }

        return ResponseEntity.ok(books);
    }
}