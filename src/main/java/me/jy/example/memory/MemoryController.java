package me.jy.example.memory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Category 5: Chat Memory & Conversational Advisors
 * Demonstrates how to maintain state and history across multiple HTTP requests,
 * allowing for conversational AI (chatbot) capabilities.
 */
@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    private final ChatClient messageMemoryChatClient;
    private final ChatClient vectorMemoryChatClient;
    private final ChatMemory sharedChatMemory;

    public MemoryController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        // In Spring AI 2.0, MessageWindowChatMemory is the standard ChatMemory implementation.
        // It defaults to storing messages in-memory using InMemoryChatMemoryRepository.
        this.sharedChatMemory = MessageWindowChatMemory.builder().build();

        // Example 15: Configured with MessageChatMemoryAdvisor (uses advisor builder pattern)
        this.messageMemoryChatClient = chatClientBuilder.clone()
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(sharedChatMemory).build())
            .build();

        // Example 16: Configured with VectorStoreChatMemoryAdvisor (uses vector store to retrieve context)
        // This is the modern, scalable alternative to the old PromptChatMemoryAdvisor.
        this.vectorMemoryChatClient = chatClientBuilder.clone()
            .defaultAdvisors(VectorStoreChatMemoryAdvisor.builder(vectorStore).build())
            .build();
    }

    /**
     * Example 15: Chat History via MessageChatMemoryAdvisor
     * Keeps history of the conversation, sending it as a series of User/Assistant messages.
     * The sessionId parameter isolates conversation histories.
     */
    @PostMapping("/message")
    public String chatWithMessageMemory(@RequestBody(required = false) MessageMemoryRequest request) {
        String sessId = (request != null && request.sessionId() != null) ? request.sessionId() : "default-session";
        String msg = (request != null && request.message() != null) ? request.message() : "Hi, my name is John Doe.";

        return this.messageMemoryChatClient.prompt()
            .user(msg)
            // The conversation ID must be passed dynamically per-call via advisor params
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessId))
            .call()
            .content();
    }

    /**
     * Example 16: Chat History via VectorStoreChatMemoryAdvisor
     * Utilizes a VectorStore to store and query conversation history semantically.
     */
    @PostMapping("/vector")
    public String chatWithVectorMemory(@RequestBody(required = false) VectorMemoryRequest request) {
        String sessId = (request != null && request.sessionId() != null) ? request.sessionId() : "default-session";
        String msg = (request != null && request.message() != null) ? request.message() : "What is my name?";

        return this.vectorMemoryChatClient.prompt()
            .user(msg)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessId))
            .call()
            .content();
    }

    /**
     * Example 17: Raw Chat Memory Storage inspection and operations
     * Demonstrates interacting directly with the ChatMemory store.
     */
    @PostMapping("/history")
    public List<Message> getRawHistory(@RequestBody(required = false) HistoryRequest request) {
        String sessId = (request != null && request.sessionId() != null) ? request.sessionId() : "default-session";
        // Retrieve the conversation history. In Spring AI 2.0, get() takes only the conversation ID.
        return this.sharedChatMemory.get(sessId);
    }

    @PostMapping("/clear")
    public String clearMemory(@RequestBody(required = false) ClearMemoryRequest request) {
        String sessId = (request != null && request.sessionId() != null) ? request.sessionId() : "default-session";
        this.sharedChatMemory.clear(sessId);
        return "Chat memory cleared for session: " + sessId;
    }

    public record MessageMemoryRequest(String sessionId, String message) {
    }

    public record VectorMemoryRequest(String sessionId, String message) {
    }

    public record HistoryRequest(String sessionId) {
    }

    public record ClearMemoryRequest(String sessionId) {
    }
}
