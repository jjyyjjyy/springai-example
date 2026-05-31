package me.jy.example.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Example 27: Model Context Protocol (MCP) Client Integration
 * Demonstrates how to connect Spring AI 2.0.0-SNAPSHOT to an MCP Server (e.g. filesystem, database, search)
 * and dynamically retrieve and execute tools.
 */
@Component
public class McpWorkflow {

    private final ChatClient chatClient;

    public McpWorkflow(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.clone().build();
    }

    /**
     * Demonstrates MCP integration:
     * 1. Outlines the code snippet required to configure and initialize an MCP client.
     * 2. Simulates an interaction where the LLM uses an MCP-exposed filesystem tool to list documents.
     */
    public McpDemoResult runWorkflow(String query) {
        // Detailed code documentation representing the actual client setup
        String mcpClientSetupCode = """
            // 1. Define Stdio transport to start and communicate with a Node.js MCP server
            McpTransport transport = new StdioClientTransport(
                ServerParameters.builder("npx")
                    .args("-y", "@modelcontextprotocol/server-filesystem", "/Users/jy/Documents")
                    .build()
            );

            // 2. Instantiate the synchronous MCP Client
            McpSyncClient mcpSyncClient = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .build();

            // 3. Establish connection handshake
            mcpSyncClient.initialize();

            // 4. Wrap MCP tools in a ToolCallbackProvider
            var toolProvider = new SyncMcpToolCallbackProvider(mcpSyncClient);

            // 5. Provide tools dynamically to ChatClient
            String response = chatClient.prompt("Summarize the contents of file notes.txt in my directory")
                .tools(toolProvider.getToolCallbacks())
                .call()
                .content();
            """;

        String explanation = "In Spring AI 2.0, MCP acts as a plug-and-play tool interface. The model automatically " +
            "requests tool arguments (e.g. read_file with path '/Users/jy/Documents/notes.txt'), the MCP client executes " +
            "the local shell command/script, and returns the output to the LLM context.";

        String mockExecutionResponse = String.format("""
            [MCP Tool Invoked: read_file({"path": "notes.txt"})]
            [MCP Output: "Meeting notes: Review Spring Boot 4 updates, configure Gemini 3.1 flash lite, write 27 progressive examples."]

            LLM Response: Based on the meeting notes in notes.txt, the three key focus areas are:
            1. Reviewing the Spring Boot 4 updates.
            2. Configuring the Gemini 3.1 flash lite model.
            3. Writing 27 progressive code examples.
            """);

        return new McpDemoResult(mcpClientSetupCode, explanation, mockExecutionResponse);
    }

    public record McpDemoResult(String mcpClientSetupCode, String explanation, String mockExecutionResponse) {
    }
}
