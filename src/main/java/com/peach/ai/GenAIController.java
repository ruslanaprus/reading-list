package com.peach.ai;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class GenAIController {

    private final ChatService chatService;
    private final RecipeService recipeService;
    private final ReadingList readingList;

    public GenAIController(ChatService chatService, RecipeService recipeService, ReadingList readingList) {
        this.chatService = chatService;
        this.recipeService = recipeService;
        this.readingList = readingList;
    }

    @PostMapping("ask-ai")
    public String getResponse(@RequestParam String prompt){
        return chatService.getResponse(prompt);
    }

    @PostMapping("ask-ai-options")
    public String getResponseOptions(@RequestParam String prompt){
        return chatService.getResponseOptions(prompt);
    }

    @PostMapping("recipe-creator")
    public String recipeCreator(@RequestParam String ingredients,
                                      @RequestParam(defaultValue = "any") String cuisine,
                                      @RequestParam(defaultValue = "") String dietaryRestrictions){
        return recipeService.createRecipe(ingredients, cuisine, dietaryRestrictions);
    }

    @PostMapping("reading-list")
    public String readingListCreator(@RequestParam(defaultValue = "5") String number,
                                     @RequestParam String genre,
                                     @RequestParam String subject,
                                     @RequestParam(defaultValue = "any") String decade,
                                     @RequestParam(defaultValue = "") String example){
        return readingList.createReadingList(number, genre, subject, decade, example);
    }

}
