package me.jy.example.agent;

import org.springaicommunity.agent.common.task.subagent.SubagentReference;
import org.springaicommunity.agent.tools.task.TaskTool;
import org.springaicommunity.agent.tools.task.claude.ClaudeSubagentReferences;
import org.springaicommunity.agent.tools.task.claude.ClaudeSubagentType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Example 31: Subagent Orchestration
 * Demonstrates hierarchical agent architectures where a main orchestrator agent
 * delegates specific subtasks to specialized subagents in isolated context windows.
 */
@Service
public class SubagentsExample {

    private final ChatClient chatClient;

    public SubagentsExample(ChatClient.Builder chatClientBuilder) {
        // Resolve subagents from resources directory
        List<SubagentReference> subagentReferences = ClaudeSubagentReferences
                .fromRootDirectory("src/main/resources/agents");

        // Build the TaskTool containing the subagent registry
        ToolCallback taskTools = TaskTool.builder()
                .subagentReferences(subagentReferences)
                .subagentTypes(ClaudeSubagentType.builder()
                        .chatClientBuilder("default", chatClientBuilder)
                        .build())
                .build();

        // Build the main ChatClient which will delegate tasks automatically
        this.chatClient = chatClientBuilder.clone()
                .defaultTools(tools -> tools.callbacks(taskTools))
                .build();
    }

    public String runExample(String userMessage) {
        return this.chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
}
