package me.jy.example.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Category 1: ChatClient API & Basics
 * Demonstrates the foundation of Spring AI 2.0.0-SNAPSHOT's fluent ChatClient API.
 */
@RestController
@RequestMapping("/api/chat")
public class SimpleChatController {

    private final ChatClient chatClient;

    // ChatClient.Builder is auto-configured and can be injected directly
    public SimpleChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.clone().build();
    }

    /**
     * Example 1: Basic Chat
     * The simplest way to invoke the LLM. It takes user input, calls the model, and returns a plain text response.
     */
    @PostMapping("/simple")
    public String simpleChat(@RequestBody(required = false) ChatRequest request) {
        String msg = (request != null && request.message() != null) ? request.message() : "Explain Spring AI in one sentence.";
        return this.chatClient.prompt()
            .user(msg)
            .call()
            .content();
    }

    /**
     * Example 2: Chat with Custom Options (Runtime override)
     * Demonstrates overriding default model parameters (like temperature, topK, topP) on a per-request basis
     * using the GoogleGenAiChatOptions builder.
     */
    @PostMapping("/options")
    public String optionsChat(@RequestBody(required = false) OptionsRequest request) {
        String msg = (request != null && request.message() != null) ? request.message() : "Write a haiku about Java 25.";
        double temp = (request != null && request.temperature() != null) ? request.temperature() : 0.9;

        return this.chatClient.prompt()
            .user(msg)
            .options(GoogleGenAiChatOptions.builder()
                .temperature(temp)
                .topP(0.95))
            .call()
            .content();
    }

    /**
     * Example 3: Streaming Chat (Reactive)
     * For long responses or interactive UI, streaming outputs tokens as they are generated.
     * In Spring AI 2.0, this is done seamlessly using Flux<String> via stream().content().
     */
    @PostMapping(value = "/stream", produces = "text/event-stream")
    public Flux<String> streamChat(@RequestBody(required = false) StreamRequest request) {
        String msg = (request != null && request.message() != null) ? request.message() : "Write a short essay on AI agents.";
        return this.chatClient.prompt()
            .user(msg)
            .stream()
            .content();
    }

    /**
     * Example 4: Prompt Templates with Parameters
     * Demonstrates using template variables in user prompts. Spring AI 2.0 ChatClient allows defining
     * templates and binding parameters directly in a fluent manner.
     */
    @PostMapping("/template")
    public String templateChat(@RequestBody(required = false) TemplateRequest request) {
        String lang = (request != null && request.language() != null) ? request.language() : "Chinese";
        String txt = (request != null && request.text() != null) ? request.text() : "I love programming in Spring Boot!";

        return this.chatClient.prompt()
            .user(u -> u.text("Translate the following text into {language}: \"{text}\"")
                .param("language", lang)
                .param("text", txt))
            .call()
            .content();
    }

    /**
     * Example 5: Message Roles (System Prompt + User Prompt)
     * Demonstrates controlling LLM behavior/personality using a System Prompt.
     * System prompt establishes context/persona, while user prompt specifies the query.
     */
    @PostMapping("/roles")
    public String roleBasedChat(@RequestBody(required = false) RolesRequest request) {
        String msg = (request != null && request.message() != null) ? request.message() : "What is the best IDE for Java?";
        String system = (request != null && request.systemPersona() != null) ? request.systemPersona() : "You are a grumpy 80-year-old COBOL developer who hates modern Java.";

        return this.chatClient.prompt()
            .system(system)
            .user(msg)
            .call()
            .content();
    }

    public record ChatRequest(String message) {
    }

    public record OptionsRequest(String message, Double temperature) {
    }

    public record StreamRequest(String message) {
    }

    public record TemplateRequest(String language, String text) {
    }

    public record RolesRequest(String message, String systemPersona) {
    }
}
