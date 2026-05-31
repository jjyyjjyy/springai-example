package me.jy.example.agent;

import org.springaicommunity.agent.advisors.AutoMemoryToolsAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Example 32: AutoMemoryTools (Durable Long-Term Memory)
 * Demonstrates how agents can persist durable facts (like user preferences
 * or project context) into structured files across multiple chat sessions.
 */
@Service
public class AutoMemoryExample {

    private final ChatClient.Builder chatClientBuilder;
    private final ConcurrentHashMap<String, ChatClient> clientCache = new ConcurrentHashMap<>();

    public AutoMemoryExample(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    public String runExample(String userMessage, String sessionId) {
        // Retrieve or create ChatClient bound to the isolated memories directory of this sessionId
        ChatClient chatClient = this.clientCache.computeIfAbsent(sessionId, id -> {
            // Define isolated directory where memory files are stored for this session.
            String memoriesDirectory = new File("memories", id).getAbsolutePath();

            // Create the AutoMemoryToolsAdvisor
            AutoMemoryToolsAdvisor memoryAdvisor = AutoMemoryToolsAdvisor.builder()
                    .memoriesRootDirectory(memoriesDirectory)
                    .build();

            // Inject the advisor into the session-specific ChatClient
            return this.chatClientBuilder.clone()
                    .defaultAdvisors(memoryAdvisor)
                    .build();
        });

        return chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID, sessionId))
                .call()
                .content();
    }
}
