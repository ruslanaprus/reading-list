package com.peach.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("ollama")
public class OllamaConfig {

    @Bean
    @Primary
    public ChatModel customOllamaChatModel(OllamaChatModel ollama) {
        return ollama;
    }

    @Bean
    public ChatOptions ollamaChatOptions(@Value("${spring.ai.ollama.chat.options.model}") String model) {
        return OllamaOptions.builder()
                .model(model)
                .temperature(0.0)
                .build();
    }
}
