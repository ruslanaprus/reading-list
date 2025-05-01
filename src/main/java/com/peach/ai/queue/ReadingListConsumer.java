package com.peach.ai.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peach.ai.books.Book;
import com.peach.ai.books.ReadingListService;
import com.peach.ai.queue.messageDTO.ReadingListMessage;
import com.peach.ai.queue.repository.ReadingListRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReadingListConsumer {
    private final ReadingListService readingListService;
    private final ObjectMapper objectMapper;
    private final ReadingListRepository readingListRepository;

    public ReadingListConsumer(ReadingListService readingListService, ObjectMapper objectMapper, ReadingListRepository readingListRepository) {
        this.readingListService = readingListService;
        this.objectMapper = objectMapper;
        this.readingListRepository = readingListRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.REQUEST_QUEUE)
    public void processReadingListRequest(String message) {
        log.info("Received message: {}", message);
        try {
            ReadingListMessage request = objectMapper.readValue(message, ReadingListMessage.class);
            List<Book> books = readingListService.createReadingList(
                    request.getRequest().getNumber(),
                    request.getRequest().getGenre(),
                    request.getRequest().getSubject(),
                    request.getRequest().getDecade(),
                    request.getRequest().getExample()
            );

            log.info("Generated reading list: {}", books);

            readingListRepository.saveResponse(request.getRequestId(), books);

        } catch (JsonProcessingException e) {
            log.error("Failed to process reading list request: {}", e.getMessage());
        }
    }
}