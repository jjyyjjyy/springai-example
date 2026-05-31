package me.jy.example.agent;

import org.springaicommunity.agent.tools.AskUserQuestionTool;
import org.springaicommunity.agent.tools.AskUserQuestionTool.Question;
import org.springaicommunity.agent.tools.AskUserQuestionTool.Question.Option;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Example 29: AskUserQuestion (Interactive Clarification)
 * Demonstrates how agents can pause execution and request structured clarification
 * or preferences from the user using multiple-choice options before answering.
 */
@Service
public class AskUserQuestionExample {

    private final ChatClient chatClient;

    public AskUserQuestionExample(ChatClient.Builder chatClientBuilder) {
        // Build AskUserQuestionTool with a simulated handler.
        // In web apps, you can use CompletableFuture/WebSockets to prompt a frontend UI.
        AskUserQuestionTool askTool = AskUserQuestionTool.builder()
                .questionHandler(questions -> {
                    Map<String, String> answers = new HashMap<>();
                    for (Question q : questions) {
                        System.out.println("[AskUserQuestion] UI Header: " + q.header());
                        System.out.println("[AskUserQuestion] Question: " + q.question());
                        for (Option opt : q.options()) {
                            System.out.println(" - " + opt.label() + ": " + opt.description());
                        }

                        // Auto-answer the first option for demonstration purposes
                        if (q.options() != null && !q.options().isEmpty()) {
                            Option choice = q.options().get(0);
                            System.out.println("[AskUserQuestion] Simulated Choice: " + choice.label());
                            answers.put(q.question(), choice.label());
                        } else {
                            answers.put(q.question(), "General Purpose");
                        }
                    }
                    return answers;
                })
                .build();

        this.chatClient = chatClientBuilder.clone()
                .defaultTools(askTool)
                .build();
    }

    public String runExample(String userMessage) {
        return this.chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
}
