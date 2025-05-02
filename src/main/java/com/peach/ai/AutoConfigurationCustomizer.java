package com.peach.ai;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.autoconfigure.vertexai.gemini.VertexAiGeminiAutoConfiguration;

/**
 * This configuration class customizes the auto-configuration behavior
 * based on which AI provider is being used.
 */
@Configuration
public class AutoConfigurationCustomizer {

    /**
     * When the "spring.profiles.active" property contains "ollama",
     * this bean will disable the Gemini auto-configuration
     */
    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "ollama")
    public static AutoConfigurationImportFilter disableGeminiForOllamaProfile() {
        return new AutoConfigurationImportFilter() {
            @Override
            public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
                boolean[] result = new boolean[autoConfigurationClasses.length];
                for (int i = 0; i < autoConfigurationClasses.length; i++) {
                    // Only disable the Gemini auto-configuration class
                    result[i] = !VertexAiGeminiAutoConfiguration.class.getName().equals(autoConfigurationClasses[i]);
                }
                return result;
            }
        };
    }

    /**
     * When the "spring.profiles.active" property contains "gemini",
     * this bean ensures the Gemini auto-configuration is applied
     */
    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "gemini")
    public static AutoConfigurationImportFilter enableGeminiForGeminiProfile() {
        return (autoConfigurationClasses, autoConfigurationMetadata) -> {
            boolean[] result = new boolean[autoConfigurationClasses.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = true;
            }
            return result;
        };
    }
}