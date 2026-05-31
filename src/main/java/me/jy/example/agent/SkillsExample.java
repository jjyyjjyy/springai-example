package me.jy.example.agent;

import org.springaicommunity.agent.tools.SkillsTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

/**
 * Example 28: Agent Skills
 * Demonstrates modular, reusable capabilities loaded dynamically.
 * Instead of hardcoding prompts, instructions are defined in SKILL.md files
 * and discovered/loaded on demand when user requests match.
 */
@Service
public class SkillsExample {

    private final ChatClient chatClient;

    public SkillsExample(ChatClient.Builder chatClientBuilder, ResourceLoader resourceLoader) {
        // Register the SkillsTool pointing to the classpath resources directory containing SKILL.md files.
        // Discovery extracts skill name and description to offer them to the model without bloating context.
        ToolCallback skillsTool = SkillsTool.builder()
                .addSkillsResource(resourceLoader.getResource("classpath:/skills"))
                .build();

        this.chatClient = chatClientBuilder.clone()
                .defaultTools(tools -> tools.callbacks(skillsTool))
                .build();
    }

    public String runExample(String userMessage) {
        // The LLM evaluates if the prompt matches the skill description.
        // If matched, it invokes the tool to load the full SKILL.md instructions into the context.
        return this.chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
}
