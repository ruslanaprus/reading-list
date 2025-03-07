package com.peach.ai.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peach.ai.books.Book;
import com.peach.ai.books.ReadingListService;
import com.peach.ai.queue.messageDTO.ReadingListMessage;
import com.peach.ai.queue.messageDTO.ReadingListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReadingListConsumer {
    private final ReadingListService readingListService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ReadingListConsumer(ReadingListService readingListService, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.readingListService = readingListService;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.REQUEST_QUEUE)
    public void processReadingListRequest(String message) {
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
            String response = objectMapper.writeValueAsString(new ReadingListResponse(request.getRequestId(), books));
            rabbitTemplate.convertAndSend(RabbitMQConfig.RESPONSE_QUEUE, response);
        } catch (JsonProcessingException e) {
            log.error("Failed to process reading list request", e);
        }
    }
}