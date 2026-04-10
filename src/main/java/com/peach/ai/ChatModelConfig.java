package com.peach.ai;

import com.google.genai.Client;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
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
    public ChatModel geminiChatModel(
            @Value("${spring.ai.google.genai.project-id}") String projectId,
            @Value("${spring.ai.google.genai.location}") String location) {
        Client genAiClient = Client.builder()
                .project(projectId)
                .location(location)
                .vertexAI(true)
                .build();
        return GoogleGenAiChatModel.builder()
                .genAiClient(genAiClient)
                .defaultOptions(GoogleGenAiChatOptions.builder()
                        .model("gemini-2.0-flash")
                        .temperature(0.0)
                        .build())
                .build();
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
        return GoogleGenAiChatOptions.builder()
                .model("gemini-2.0-flash")
                .temperature(0.0)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "chat.model", havingValue = "ollama")
    public ChatOptions ollamaChatOptions(@Value("${spring.ai.ollama.chat.options.model}") String model) {
        return OllamaChatOptions.builder()
                .model(model)
                .temperature(0.0)
                .build();
    }

}
