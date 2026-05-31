package me.jy.example.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.session.DefaultSessionService;
import org.springframework.ai.session.InMemorySessionRepository;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionMemoryAdvisor;
import org.springframework.stereotype.Service;

/**
 * Example 33: Session Management
 * Demonstrates the event-sourced session memory layer.
 * Tracks conversation history as event sequences, implementing turn-aware compaction
 * to avoid breaking tool-calling sequences or exceeding context windows.
 */
@Service
public class SessionManagementExample {

    private final ChatClient chatClient;

    public SessionManagementExample(ChatClient.Builder chatClientBuilder) {
        // Create the InMemorySessionRepository and DefaultSessionService
        InMemorySessionRepository sessionRepository = InMemorySessionRepository.builder().build();

        SessionService sessionService = DefaultSessionService.builder()
                .sessionRepository(sessionRepository)
                .build();

        // Create the SessionMemoryAdvisor. By default, it will manage and compact the session conversation.
        SessionMemoryAdvisor sessionMemoryAdvisor = SessionMemoryAdvisor.builder(sessionService)
                .defaultUserId("user-id-example")
                .build();

        // Build the ChatClient with the SessionMemoryAdvisor
        this.chatClient = chatClientBuilder.clone()
                .defaultAdvisors(sessionMemoryAdvisor)
                .build();
    }

    public String runExample(String userMessage, String sessionId) {
        // Prompt the model, providing the session ID in the advisor context parameter
        return this.chatClient.prompt()
                .user(userMessage)
                .advisors(context -> context.param(SessionMemoryAdvisor.SESSION_ID_CONTEXT_KEY, sessionId))
                .call()
                .content();
    }
}
