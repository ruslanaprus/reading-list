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
import org.springframework.core.env.Environment;

@Configuration
public class ChatModelConfig {

    private static final String API_KEY_PROP = "spring.ai.google.genai.api-key";
    private static final String PROJECT_ID_PROP = "spring.ai.google.genai.project-id";
    private static final String LOCATION_PROP = "spring.ai.google.genai.location";

    @Bean
    @Primary
    @ConditionalOnProperty(name = "chat.model", havingValue = "gemini")
    public ChatModel geminiChatModel(Environment env) {
        String apiKey = env.getProperty(API_KEY_PROP);
        String projectId = env.getProperty(PROJECT_ID_PROP);
        String location = env.getProperty(LOCATION_PROP);

        Client.Builder clientBuilder = Client.builder();

        if (apiKey != null && !apiKey.isBlank()) {
            clientBuilder.apiKey(apiKey);
        } else if (projectId != null && location != null) {
            clientBuilder.project(projectId)
                    .location(location)
                    .vertexAI(true);
        } else {
            throw new IllegalStateException(
                    "Either '" + API_KEY_PROP + "' or ('" + PROJECT_ID_PROP + "' + '" + LOCATION_PROP + "') must be configured");
        }

        return GoogleGenAiChatModel.builder()
                .genAiClient(clientBuilder.build())
                .defaultOptions(GoogleGenAiChatOptions.builder()
                        .model("gemini-3.1-flash-lite-preview")
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
                .model("gemini-3.1-flash-lite-preview")
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
