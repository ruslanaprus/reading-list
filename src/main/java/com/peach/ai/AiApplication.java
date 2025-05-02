package com.peach.ai;

import org.springframework.ai.autoconfigure.vertexai.gemini.VertexAiGeminiAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { VertexAiGeminiAutoConfiguration.class })
public class AiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiApplication.class, args);
	}

}
