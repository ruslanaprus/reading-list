package com.peach.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("ollama")
public class OllamaConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "chat.model", havingValue = "ollama")
    public ChatModel customOllamaChatModel(OllamaChatModel ollama) {
        return ollama;
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
