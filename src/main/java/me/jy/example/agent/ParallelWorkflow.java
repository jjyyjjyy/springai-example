package me.jy.example.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example 23: Parallelization Workflow
 * Executes independent sub-tasks concurrently and aggregates their results.
 * This pattern reduces overall latency and isolates concerns for multi-aspect evaluations.
 */
@Component
public class ParallelWorkflow {

    private final ChatClient chatClient;
    // We define a thread pool for parallel operations
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public ParallelWorkflow(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.clone().build();
    }

    /**
     * Executes parallel tasks on the input text:
     * - Task 1: Grammar and spelling review.
     * - Task 2: SEO keyword enrichment recommendations.
     * - Task 3: Policy / Safety check.
     */
    public ParallelReport runWorkflow(String text) {
        // Task 1: Grammar review
        CompletableFuture<String> grammarFuture = CompletableFuture.supplyAsync(() -> {
            return this.chatClient.prompt()
                .user("Proofread the following text for grammar, punctuation, and style. List corrections if any:\n" + text)
                .call()
                .content();
        }, executorService);

        // Task 2: SEO check
        CompletableFuture<String> seoFuture = CompletableFuture.supplyAsync(() -> {
            return this.chatClient.prompt()
                .user("Analyze the following text for SEO purposes. Recommend 3 relevant target keywords and suggestions for better reach:\n" + text)
                .call()
                .content();
        }, executorService);

        // Task 3: Safety/Policy check
        CompletableFuture<String> safetyFuture = CompletableFuture.supplyAsync(() -> {
            return this.chatClient.prompt()
                .user("Check the following text for offensive language, hate speech, or sensitive policy violations. Output either 'SAFE' or a list of issues found:\n" + text)
                .call()
                .content();
        }, executorService);

        // Wait for all three tasks to complete
        CompletableFuture.allOf(grammarFuture, seoFuture, safetyFuture).join();

        try {
            return new ParallelReport(
                text,
                grammarFuture.get(),
                seoFuture.get(),
                safetyFuture.get()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error processing parallel tasks", e);
        }
    }

    public record ParallelReport(String originalText, String grammarCheck, String seoOptimization,
                                 String safetyEvaluation) {
    }
}
