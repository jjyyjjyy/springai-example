package me.jy.example.tool;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * A Spring Component containing methods annotated with @Tool.
 * Spring AI will scan these methods and expose them as tools to the LLM.
 */
@Component
public class WeatherService {

    static void main() {
        System.out.println(
            """
                # Executive Report: Model Context Protocol (MCP) in Enterprise AI Architecture\n\n**To:** Chief Technology Officer / Enterprise Architecture Board  \n**From:** Principal Business Consultant  \n**Date:** May 22, 2024  \n**Subject:** Strategic Assessment of Model Context Protocol (MCP) for Enterprise AI Scalability\n\n---\n\n## 1. Executive Summary\nAs enterprises transition from experimental AI chatbots to autonomous agentic workflows, the primary bottleneck has shifted from \"model intelligence\" to \"context connectivity.\" The **Model Context Protocol (MCP)** emerges as a critical open-standard framework designed to solve the \"N+1\" integration problem. By decoupling AI models from data silos, MCP provides a universal interface for RAG (Retrieval-Augmented Generation) and tool-use, positioning itself as the potential \"HTTP of the AI era.\"\n\n---\n\n## 2. Strategic SWOT Analysis\n\n| **Strengths** | **Weaknesses** |\n| :--- | :--- |\n| **Universal Standardization:** Eliminates redundant custom connectors across different AI applications. | **Early Maturity:** Documentation and enterprise-grade governance tools are still in nascent stages. |\n| **Vendor Agnostic:** Breaks proprietary lock-in, allowing seamless switching between LLM providers. | **Legacy Friction:** Requires dedicated engineering to wrap legacy enterprise systems in MCP-compliant servers. |\n| **Opportunities** | **Threats** |\n| **Agentic Acceleration:** Enables agents to execute multi-step workflows (e.g., triggering CI/CD, updating CRM). | **Market Fragmentation:** Potential for major cloud providers to prioritize proprietary standards over open protocols. |\n| **Data Sovereignty:** Facilitates local/private LLM deployments without exposing sensitive data to third-party APIs. | **Security Surface:** Standardized access to enterprise data requires rigorous, centralized auditing to prevent unauthorized data exposure. |\n\n---\n\n## 3. Competitive Landscape & Strategic Positioning\nThe current market for \"context connectivity\" is divided into three architectural archetypes. Understanding where MCP fits is vital for infrastructure planning:\n\n*   **Standardized Protocols (Direct Competitors):** *LlamaIndex, LangChain.* These offer mature ecosystems but often suffer from \"library lock-in.\" MCP differentiates by being a protocol rather than a framework.\n*   **Orchestration Frameworks:** *Microsoft Semantic Kernel.* Provides a cohesive, opinionated stack for enterprise environments. It is a strong alternative for organizations deeply embedded in the .NET/Azure ecosystem.\n*   **Data-to-AI Middleware:** *Airbyte, Fivetran, Zapier.* These focus on data movement or low-code automation. While easier to deploy, they often lack the real-time, bi-directional interaction required for high-performance agentic workflows.\n\n**Strategic Insight:** MCP serves as the ideal **abstraction layer**. By adopting MCP, the enterprise decouples its data access layer from its AI model layer, ensuring long-term architectural agility.\n\n---\n\n## 4. Future Outlook: Key Trends\nThe evolution of MCP will likely follow these four critical trajectories:\n\n1.  **From Retrieval to Execution:** The focus will shift from simple document retrieval to active \"agentic orchestration,\" where MCP servers allow agents to perform state-changing operations across enterprise systems.\n2.  **Security as a Protocol Feature:** Future iterations will treat MCP servers as \"Policy Enforcement Points,\" integrating directly with existing IAM (Identity and Access Management) to provide granular audit trails and RBAC.\n3.  **Marketplace Commoditization:** Expect a surge in \"official\" MCP servers from SaaS vendors (e.g., Salesforce, Jira), effectively turning data ingestion into a plug-and-play commodity.\n4.  **Operational Maturity:** To mitigate latency and management complexity, we anticipate the emergence of **\"MCP Orchestrators\"**—middleware that manages connection pooling, caching, and discovery for large-scale enterprise deployments.\n\n---\n\n## 5. Consultant’s Recommendation\nThe adoption of MCP is a strategic hedge against technical debt.\n\n*   **Adopt MCP if:** Your enterprise prioritizes **vendor neutrality**, has a diverse stack of LLMs/models, and requires a sustainable, scalable way to connect internal data silos to AI agents.\n*   **Prioritize Alternatives (e.g., Semantic Kernel) if:** Your organization requires immediate, \"out-of-the-box\" support, high-touch vendor SLAs, and is already heavily invested in a specific cloud-native ecosystem.\n\n**Final Verdict:** For organizations building a long-term AI strategy, **MCP is the superior architectural choice.** It transforms AI connectivity from a bespoke, brittle engineering task into a modular, standardized infrastructure component. We recommend a phased pilot program focusing on high-value, low-risk internal data repositories to evaluate integration overhead before full-scale deployment.
                """

        );
    }

    /**
     * Example 11: Tool via @Tool Annotation
     * Standard method tool. Spring AI auto-generates the JSON schema for parameters (city)
     * based on the Java signature and description.
     */
    @Tool(description = "Get the current weather forecast (temperature, condition) for a specific city.")
    public String getCityWeather(String city) {
        String cleanedCity = city.trim().toLowerCase();
        return switch (cleanedCity) {
            case "san francisco", "sf" -> "Current weather in San Francisco: 14°C, Foggy, Wind 15 km/h NW.";
            case "beijing", "北京" -> "Current weather in Beijing: 26°C, Sunny, Wind 5 km/h E.";
            case "tokyo" -> "Current weather in Tokyo: 20°C, Rain, Wind 25 km/h S.";
            default -> "Current weather in " + city + ": 22°C, Partly Cloudy.";
        };
    }

    /**
     * Example 13: Tool with ToolContext
     * Demonstrates accessing request-specific metadata (like current user's name or authorization token)
     * passed from the controller via ToolContext. The ToolContext is excluded from the LLM schema.
     */
    @Tool(description = "Retrieve the personalized welcome greeting and message for the current user.")
    public String getPersonalizedMessage(ToolContext toolContext) {
        if (toolContext == null || toolContext.getContext() == null) {
            return "Hello guest! Welcome to the weather station.";
        }

        String username = (String) toolContext.getContext().getOrDefault("username", "Guest");
        String tier = (String) toolContext.getContext().getOrDefault("tier", "Free");

        return String.format("Hello %s (Tier: %s)! We hope you enjoy using our weather tool today.", username, tier);
    }
}
