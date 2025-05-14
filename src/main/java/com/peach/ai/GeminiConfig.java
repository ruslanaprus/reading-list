package com.peach.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("gemini")
public class GeminiConfig {

    @Bean
    @Primary
    public ChatModel geminiChatModel(VertexAiGeminiChatModel gemini) {
        return gemini;
    }

    @Bean
    public ChatOptions geminiChatOptions() {
        return VertexAiGeminiChatOptions.builder()
                .temperature(0.0)
                .build();
    }
}
