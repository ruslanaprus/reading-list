package com.peach.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReadingList {

    private final ChatModel chatModel;

    public ReadingList(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String createReadingList(String number,
                                    String genre,
                                    String subject,
                                    String decade,
                                    String example) {

        var template = """
                I want to create a personalized reading list of **{number}** books in the **{genre}** genre. \s
                I am interested in books about **{subject}**. \s
                
                - If possible, prioritize books written during **{decade}**. (If not provided, ignore this criterion.) \s
                - If an example book is given (**{example}**), please consider its style, themes, or writing approach when making recommendations. \s
                
                For each book, please provide: \s
                - Title \s
                - Author \s
                - Number of pages \s
                - Average rating from Goodreads \s
                - A brief summary explaining why it fits my criteria. \s
                
                Do not hallucinate.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Map<String, Object> params = Map.of(
                "number", number,
                "genre", genre,
                "subject", subject,
                "decade", decade,
                "example", example
        );

        Prompt prompt = promptTemplate.create(params);
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

}
