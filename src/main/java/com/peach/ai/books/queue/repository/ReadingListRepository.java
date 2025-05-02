package com.peach.ai.books.queue.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peach.ai.books.model.Book;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class ReadingListRepository {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final long EXPIRATION_TIME_MINUTES = 60;

    public ReadingListRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveResponse(String requestId, List<Book> books) {
        try {
            String json = objectMapper.writeValueAsString(books);
            redisTemplate.opsForValue().set(requestId, json, EXPIRATION_TIME_MINUTES, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to store reading list", e);
        }
    }

    public List<Book> getResponse(String requestId) {
        String json = redisTemplate.opsForValue().get(requestId);
        if (json == null) return null;

        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to retrieve reading list", e);
        }
    }
}
