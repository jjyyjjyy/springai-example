package me.jy.example.structured;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Category 2: Structured Output Converters
 * Demonstrates how to parse unstructured LLM output into strongly-typed Java objects,
 * maps, or lists natively using ChatClient's entity() mechanism.
 */
@RestController
@RequestMapping("/api/structured")
public class StructuredController {

    private final ChatClient chatClient;

    public StructuredController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.clone().build();
    }

    /**
     * Example 6: Bean Output Converter (Entity Mapping)
     * Automatically requests and parses JSON conforming to the specified Java record class.
     * Behind the scenes, Spring AI injects formatting instructions and parses the response.
     */
    @PostMapping("/bean")
    public BookDetails getBookDetails(@RequestBody(required = false) BookRequest request) {
        String name = (request != null && request.bookName() != null) ? request.bookName() : "The Great Gatsby";
        return this.chatClient.prompt()
            .user(u -> u.text("Provide detailed info about the book: {bookName}")
                .param("bookName", name))
            .call()
            .entity(BookDetails.class);
    }

    /**
     * Example 7: Map Output Converter
     * Useful when the target structure is dynamic or doesn't have a pre-defined Java class.
     * Uses ParameterizedTypeReference to instruct Spring AI to parse the result into a Map.
     */
    @PostMapping("/map")
    public Map<String, Object> getCompanyStats(@RequestBody(required = false) CompanyRequest request) {
        String name = (request != null && request.companyName() != null) ? request.companyName() : "Google";
        return this.chatClient.prompt()
            .user(u -> u.text("Provide corporate stats for {companyName}. Include attributes like 'ceo', 'headquarters', 'foundedYear', and 'ticker'.")
                .param("companyName", name))
            .call()
            .entity(new ParameterizedTypeReference<Map<String, Object>>() {
            });
    }

    /**
     * Example 8: List Output Converter
     * Automatically formats the output into a comma-separated list or JSON array and parses it into a Java List.
     */
    @PostMapping("/list")
    public List<String> getProgrammingLanguages(@RequestBody(required = false) DomainRequest request) {
        String dom = (request != null && request.domain() != null) ? request.domain() : "frontend";
        return this.chatClient.prompt()
            .user(u -> u.text("List top 5 programming languages or frameworks used in {domain} development.")
                .param("domain", dom))
            .call()
            .entity(new ParameterizedTypeReference<List<String>>() {
            });
    }

    // Java record to represent structured book details
    public record BookDetails(String title, String author, List<String> genres, int publishYear, String summary) {
    }

    // Request records for request bodies
    public record BookRequest(String bookName) {
    }

    public record CompanyRequest(String companyName) {
    }

    public record DomainRequest(String domain) {
    }
}
