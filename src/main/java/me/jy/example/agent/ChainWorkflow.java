package me.jy.example.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Example 22: Chain Workflow (Prompt Chaining)
 * Breaks down a complex multi-step task into sequential steps.
 * The output of each step is fed into the prompt of the subsequent step.
 */
@Component
public class ChainWorkflow {

    private final ChatClient chatClient;

    public ChainWorkflow(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.clone().build();
    }

    /**
     * Executes the Chain Workflow:
     * Step 1: Translate non-English input text to English.
     * Step 2: Perform sentiment analysis (Positive, Neutral, Negative) on the English translation.
     * Step 3: Draft an appropriate customer email response based on the sentiment and text.
     */
    public ChainResult runWorkflow(String inputText) {
        // Step 1: Translate to English
        String translationPrompt = String.format("""
            Translate the following text into clear, standard English.
            If the text is already in English, output it exactly as is.
            Text to translate:
            "%s"
            """, inputText);

        String translation = this.chatClient.prompt()
            .user(translationPrompt)
            .call()
            .content();

        // Step 2: Sentiment Analysis
        String sentimentPrompt = String.format("""
            Analyze the sentiment of the following English text.
            Respond with exactly one of the following words: POSITIVE, NEUTRAL, NEGATIVE.
            Text:
            "%s"
            """, translation);

        String sentiment = this.chatClient.prompt()
            .user(sentimentPrompt)
            .call()
            .content()
            .trim()
            .toUpperCase();

        // Step 3: Response Drafting
        String draftingPrompt = String.format("""
            You are a professional customer support agent.
            Draft a polite response email to a user who wrote the following message.
            The analyzed sentiment of the user's message is: %s.
            User Message:
            "%s"

            Keep the response concise (under 150 words).
            """, sentiment, translation);

        String responseDraft = this.chatClient.prompt()
            .user(draftingPrompt)
            .call()
            .content();

        return new ChainResult(inputText, translation, sentiment, responseDraft);
    }

    public record ChainResult(String originalInput, String translation, String sentiment, String responseDraft) {
    }
}
