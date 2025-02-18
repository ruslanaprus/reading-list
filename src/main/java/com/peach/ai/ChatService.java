package com.peach.ai;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final VertexAiGeminiChatModel chatModel;

    public ChatService(VertexAiGeminiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String getResponse(String prompt){
        return chatModel.call(prompt);
    }

    public String getResponseOptions(String prompt){
        ChatResponse chatResponse = chatModel.call(
                new Prompt(
                        prompt,
                        VertexAiGeminiChatOptions.builder()
                                .temperature(0.4)
                                .build()
                ));
        return chatResponse.getResult().getOutput().getContent();
    }
}
