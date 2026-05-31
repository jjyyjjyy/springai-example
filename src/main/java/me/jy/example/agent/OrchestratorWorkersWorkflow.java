package me.jy.example.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example 25: Orchestrator-Workers Workflow
 * A central LLM (Orchestrator) decomposes a complex task into multiple sub-tasks,
 * delegates them to separate worker calls (concurrently), and then aggregates the workers'
 * results into a final consolidated response.
 */
@Component
public class OrchestratorWorkersWorkflow {

    private final ChatClient chatClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public OrchestratorWorkersWorkflow(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.clone().build();
    }

    /**
     * Executes the Orchestrator-Workers Workflow:
     * 1. Orchestrator LLM breaks down the target topic into 3 distinct analytical sub-tasks.
     * 2. Worker LLMs (represented by separate prompt executions) process their specific sub-tasks in parallel.
     * 3. Orchestrator LLM aggregates all worker outputs and writes a comprehensive final report.
     */
    public OrchestratorResult runWorkflow(String topic) {
        // Step 1: Orchestrator decomposes the topic.
        // We'll define a set of analytical sub-tasks to run on the topic.
        List<SubTask> subTasks = List.of(
            new SubTask("SWOT Analysis", "Identify Strengths, Weaknesses, Opportunities, and Threats",
                "Perform a detailed SWOT analysis for the topic: " + topic),
            new SubTask("Competitor Landscape", "Identify direct and indirect competitors or alternatives",
                "Analyze the competitor landscape or similar options for the topic: " + topic),
            new SubTask("Future Outlook", "Identify trends and predictions for the next 5 years",
                "Evaluate the future outlook and key trends for the topic: " + topic)
        );

        // Step 2: Delegate to workers concurrently
        List<CompletableFuture<SubTaskResult>> futures = subTasks.stream()
            .map(subTask -> CompletableFuture.supplyAsync(() -> {
                String workerOutput = this.chatClient.prompt()
                    .system("You are a specialized research analyst worker. Focus only on the requested section.")
                    .user(subTask.promptForWorker())
                    .call()
                    .content();
                return new SubTaskResult(subTask.name(), workerOutput);
            }, executorService))
            .toList();

        // Wait for all workers to finish
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<SubTaskResult> results = futures.stream()
            .map(CompletableFuture::join)
            .toList();

        // Step 3: Orchestrator compiles the final report
        StringBuilder compiledInputs = new StringBuilder();
        for (SubTaskResult res : results) {
            compiledInputs.append("### ").append(res.taskName()).append("\n")
                .append(res.output()).append("\n\n");
        }

        String compilationPrompt = String.format("""
            You are a principal business consultant.
            Compile the following sub-analysis sections into a professional, cohesive executive report for the topic: "%s".
            Synthesize and format the information beautifully.

            Sub-analyses:
            %s
            """, topic, compiledInputs.toString());

        String finalCompiledReport = this.chatClient.prompt()
            .user(compilationPrompt)
            .call()
            .content();

        return new OrchestratorResult(topic, results, finalCompiledReport);
    }

    public record SubTask(String name, String description, String promptForWorker) {
    }

    public record SubTaskResult(String taskName, String output) {
    }

    public record OrchestratorResult(String topic, List<SubTaskResult> workerOutputs, String finalCompiledReport) {
    }
}
