package me.jy.example.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Example 24: Routing Workflow
 * Dynamically classifies input and routes it to a specialized LLM persona/handler.
 * Isolating concerns into specific routing endpoints yields much higher quality answers.
 */
@Component
public class RoutingWorkflow {

    private final ChatClient chatClient;

    public RoutingWorkflow(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.clone().build();
    }

    /**
     * Executes the Routing Workflow:
     * 1. Classify the user query into: BILLING, TECHNICAL, or GENERAL.
     * 2. Direct the query to a specialized prompt based on the classification.
     */
    public RoutingResult runWorkflow(String query) {
        // Step 1: Classify the input
        String classificationPrompt = String.format("""
            Classify the following customer service inquiry into exactly one of three categories:
            - BILLING (refunds, invoicing, subscription issues)
            - TECHNICAL (app crashes, login errors, bugs, API integration issues)
            - GENERAL (general questions, feedback, partnerships)

            Respond with only the category name in uppercase.
            Query: "%s"
            """, query);

        String category = this.chatClient.prompt()
            .user(classificationPrompt)
            .call()
            .content()
            .trim()
            .toUpperCase();

        // Ensure category is sanitized
        if (!category.equals("BILLING") && !category.equals("TECHNICAL")) {
            category = "GENERAL";
        }

        // Step 2: Handle based on classification
        String response;
        switch (category) {
            case "BILLING" -> response = handleBilling(query);
            case "TECHNICAL" -> response = handleTechnical(query);
            default -> response = handleGeneral(query);
        }

        return new RoutingResult(query, category, response);
    }

    private String handleBilling(String query) {
        return this.chatClient.prompt()
            .system("You are an expert Billing Support Specialist. Be extremely polite, precise, and outline refund/invoice policies clearly.")
            .user(query)
            .call()
            .content();
    }

    private String handleTechnical(String query) {
        return this.chatClient.prompt()
            .system("You are a Senior Technical Support Engineer. Be analytical, request relevant logs or steps, and provide code/troubleshooting steps.")
            .user(query)
            .call()
            .content();
    }

    private String handleGeneral(String query) {
        return this.chatClient.prompt()
            .system("You are a Helpful General Support Assistant. Provide a warm, friendly response addressing the inquiry.")
            .user(query)
            .call()
            .content();
    }

    public record RoutingResult(String query, String category, String specialistResponse) {
    }
}
