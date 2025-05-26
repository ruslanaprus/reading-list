package com.peach.ai.books.web;

import com.peach.ai.books.model.Book;
import com.peach.ai.books.model.ReadingListRequest;
import com.peach.ai.books.queue.repository.ReadingListRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebController {

    private final com.peach.ai.books.BookController bookController;
    private final ReadingListRepository readingListRepository;
    
    // Store requests and their IDs for browsing
    private final Map<ReadingListRequest, String> requestToIdMap = new ConcurrentHashMap<>();

    public WebController(com.peach.ai.books.BookController bookController,
                         ReadingListRepository readingListRepository) {
        this.bookController = bookController;
        this.readingListRepository = readingListRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("request", new ReadingListRequest());
        return "index";
    }

    @PostMapping("/search")
    public String search(@ModelAttribute ReadingListRequest request, RedirectAttributes redirectAttributes) {
        String requestBody = bookController.readingListCreator(request).getBody();
        String requestId = requestBody.split("requestId: ")[1].replaceAll(" to retrieve the results\\.", "");
        
        // Store the request for browsing
        requestToIdMap.put(new ReadingListRequest(
            request.getNumber(), 
            request.getGenre(), 
            request.getSubject(), 
            request.getDecade(), 
            request.getExample()
        ), requestId);
        
        redirectAttributes.addAttribute("requestId", requestId);
        return "redirect:/results/" + requestId;
    }
    
    @GetMapping("/results/{requestId}")
    public String showResults(@PathVariable String requestId, Model model) {
        model.addAttribute("requestId", requestId);
        
        List<Book> books = readingListRepository.getResponse(requestId);
        if (books != null) {
            model.addAttribute("books", books);
            model.addAttribute("ready", true);
        } else {
            model.addAttribute("ready", false);
        }
        
        return "results";
    }
    
    @GetMapping("/browse")
    public String browseLists(Model model) {
        Map<ReadingListRequest, List<Book>> readingLists = new HashMap<>();
        Map<ReadingListRequest, String> readingListIds = new HashMap<>();
        
        requestToIdMap.forEach((request, id) -> {
            List<Book> books = readingListRepository.getResponse(id);
            if (books != null) {
                readingLists.put(request, books);
                readingListIds.put(request, id);
            }
        });
        
        model.addAttribute("readingLists", readingLists);
        model.addAttribute("readingListIds", readingListIds);
        
        return "browse";
    }
}
