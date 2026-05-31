package me.jy.example.agent;

import org.springaicommunity.agent.tools.TodoWriteTool;
import org.springaicommunity.agent.tools.TodoWriteTool.Todos.TodoItem;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.ToolCallAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Example 30: TodoWrite (Structured Task Planning)
 * Explores how task planning can be made explicit and observable.
 * Prevents "lost in the middle" context window issues by decomposing
 * complex requests into discrete steps that the agent tracks sequentially.
 */
@Service
public class TodoWriteExample {

    private final ChatClient chatClient;
    private static final ThreadLocal<List<List<TodoItem>>> progressCollector = ThreadLocal.withInitial(ArrayList::new);

    public TodoWriteExample(ChatClient.Builder chatClientBuilder) {
        // Register TodoWriteTool with a custom event handler for real-time progress updates
        TodoWriteTool todoWriteTool = TodoWriteTool.builder()
                .todoEventHandler(event -> {
                    List<TodoItem> todos = event.todos();
                    // Capture progress state in the thread-local collector
                    progressCollector.get().add(new ArrayList<>(todos));

                    long completed = todos.stream()
                            .filter(t -> "completed".equalsIgnoreCase(t.status().name()))
                            .count();
                    long total = todos.size();
                    System.out.printf("[TodoWrite] Progress: %d/%d tasks completed (%.0f%%)%n",
                            completed, total, (total == 0 ? 0.0 : (completed * 100.0 / total)));
                    for (TodoItem item : todos) {
                        System.out.printf("  [%s] %s%n", item.status(), item.content());
                    }
                })
                .build();

        // Enforce sequential tool calling and advisors to capture and store reasoning
        this.chatClient = chatClientBuilder.clone()
                .defaultSystem("""
                        You are a structured planning and execution agent.
                        When the user gives you a request, you MUST use the TodoWriteTool to track your progress:
                        1. First, call TodoWriteTool to create a checklist of all steps required to fulfill the request. Initially, all steps must have status 'pending'.
                        2. For each step in the list, perform it sequentially. If a step cannot be physically executed (e.g., 'building a house' or 'excavation'), you must simulate its execution by explaining the step, then call TodoWriteTool to mark it as 'completed'.
                        3. You must call TodoWriteTool to update the status for each individual step one by one. Do not complete multiple steps in a single tool call.
                        4. Continue this cycle until all checklist items are marked as 'completed'.
                        5. Only output your final response to the user after all steps are marked 'completed'.
                        """)
                .defaultTools(todoWriteTool)
                .defaultAdvisors(
                        ToolCallAdvisor.builder().conversationHistoryEnabled(false).build(),
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build()
                )
                .build();
    }

    public TodoExampleResult runExample(String userMessage, String sessionId) {
        // Clear thread-local collector before starting execution
        progressCollector.get().clear();

        try {
            String answer = this.chatClient.prompt()
                    .user(userMessage)
                    .advisors(a -> a.param(org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID, sessionId))
                    .call()
                    .content();

            return new TodoExampleResult(answer, new ArrayList<>(progressCollector.get()));
        } finally {
            // Clean up ThreadLocal to prevent memory leak
            progressCollector.remove();
        }
    }

    public record TodoExampleResult(String answer, List<List<TodoItem>> progressHistory) {}
}
