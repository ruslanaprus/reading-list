package com.peach.ai;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gemini")
@ConditionalOnProperty(name = "chat.model", havingValue = "gemini")
public class GeminiController {

    private VertexAiGeminiChatModel chatModel;

    public GeminiController(VertexAiGeminiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        Prompt prompt = new Prompt(message);
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}