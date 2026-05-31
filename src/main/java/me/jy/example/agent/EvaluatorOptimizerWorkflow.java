package me.jy.example.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Example 26: Evaluator-Optimizer Workflow
 * Implements an iterative feedback loop: one LLM (Generator) creates a draft,
 * another LLM (Evaluator) evaluates it against criteria and gives feedback/score.
 * If the score is below the threshold, the generator optimizes the draft using the feedback.
 */
@Component
public class EvaluatorOptimizerWorkflow {

    private final ChatClient chatClient;

    public EvaluatorOptimizerWorkflow(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.clone().build();
    }

    /**
     * Executes the Evaluator-Optimizer Workflow:
     * Drafts and refines a poem about the specified coding topic until it achieves an evaluator score of >= 8.
     */
    public WorkflowResult runWorkflow(String topic) {
        List<IterationLog> iterations = new ArrayList<>();
        String currentDraft = "";
        String feedback = "Initial draft generation.";
        int maxIterations = 3;
        int targetScore = 8;

        for (int i = 1; i <= maxIterations; i++) {
            // Step 1: Generate / Optimize Draft
            String generationPrompt;
            if (i == 1) {
                generationPrompt = String.format("Write a 4-line rhyming poem about '%s'. Ensure it rhymes and is creative.", topic);
            } else {
                generationPrompt = String.format("""
                    Optimize the following poem about '%s' using the feedback provided.
                    Current Poem:
                    "%s"

                    Feedback for improvement:
                    %s

                    Output ONLY the revised 4-line poem, nothing else.
                    """, topic, currentDraft, feedback);
            }

            currentDraft = this.chatClient.prompt()
                .user(generationPrompt)
                .call()
                .content()
                .trim();

            // Step 2: Evaluate Draft
            Evaluation evaluation = evaluateDraft(currentDraft, topic, targetScore);
            iterations.add(new IterationLog(i, currentDraft, evaluation));

            // Check if passed
            if (evaluation.passed()) {
                break;
            }
            feedback = evaluation.feedback();
        }

        return new WorkflowResult(topic, iterations, currentDraft);
    }

    private Evaluation evaluateDraft(String draft, String topic, int targetScore) {
        String evaluationPrompt = String.format("""
            You are a critical poetry evaluator.
            Evaluate the following poem on a scale of 1 to 10 (10 being perfect).
            The poem must be about the topic: "%s".

            Poem:
            "%s"
            """, topic, draft);

        EvaluationResponse response = this.chatClient.prompt()
            .user(evaluationPrompt)
            .call()
            .entity(EvaluationResponse.class);

        int score = response != null ? response.score() : 5;
        String feedback = response != null && response.feedback() != null ? response.feedback() : "No feedback provided.";
        boolean passed = score >= targetScore;
        return new Evaluation(score, feedback, passed);
    }

    public record Evaluation(int score, String feedback, boolean passed) {
    }

    public record EvaluationResponse(int score, String feedback) {
    }

    public record IterationLog(int iteration, String draft, Evaluation evaluation) {
    }

    public record WorkflowResult(String topic, List<IterationLog> iterations, String finalResult) {
    }
}
