package com.peach.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatModelConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "chat.model", havingValue = "gemini")
    public ChatModel geminiChatModel(VertexAiGeminiChatModel gemini) {
        return gemini;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "chat.model", havingValue = "ollama")
    public ChatModel customOllamaChatModel(OllamaChatModel ollama) {
        return ollama;
    }

    @Bean
    @ConditionalOnProperty(name = "chat.model", havingValue = "gemini")
    public ChatOptions geminiChatOptions() {
        return VertexAiGeminiChatOptions.builder()
                .temperature(0.0)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "chat.model", havingValue = "ollama")
    public ChatOptions ollamaChatOptions(@Value("${spring.ai.ollama.chat.options.model}") String model) {
        return OllamaOptions.builder()
                .model(model)
                .temperature(0.0)
                .build();
    }

}
