package me.jy.example.agent;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Category 7: Agentic Patterns & Workflows REST Controller
 * Exposes endpoints to trigger and inspect intermediate execution steps
 * for each of the agentic workflow patterns and memory/session tools.
 */
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final ChainWorkflow chainWorkflow;
    private final ParallelWorkflow parallelWorkflow;
    private final RoutingWorkflow routingWorkflow;
    private final OrchestratorWorkersWorkflow orchestratorWorkersWorkflow;
    private final EvaluatorOptimizerWorkflow evaluatorOptimizerWorkflow;
    private final McpWorkflow mcpWorkflow;
    private final SkillsExample skillsExample;
    private final AskUserQuestionExample askUserQuestionExample;
    private final TodoWriteExample todoWriteExample;
    private final SubagentsExample subagentsExample;
    private final AutoMemoryExample autoMemoryExample;
    private final SessionManagementExample sessionManagementExample;

    public AgentController(ChainWorkflow chainWorkflow,
                           ParallelWorkflow parallelWorkflow,
                           RoutingWorkflow routingWorkflow,
                           OrchestratorWorkersWorkflow orchestratorWorkersWorkflow,
                           EvaluatorOptimizerWorkflow evaluatorOptimizerWorkflow,
                           McpWorkflow mcpWorkflow,
                           SkillsExample skillsExample,
                           AskUserQuestionExample askUserQuestionExample,
                           TodoWriteExample todoWriteExample,
                           SubagentsExample subagentsExample,
                           AutoMemoryExample autoMemoryExample,
                           SessionManagementExample sessionManagementExample) {
        this.chainWorkflow = chainWorkflow;
        this.parallelWorkflow = parallelWorkflow;
        this.routingWorkflow = routingWorkflow;
        this.orchestratorWorkersWorkflow = orchestratorWorkersWorkflow;
        this.evaluatorOptimizerWorkflow = evaluatorOptimizerWorkflow;
        this.mcpWorkflow = mcpWorkflow;
        this.skillsExample = skillsExample;
        this.askUserQuestionExample = askUserQuestionExample;
        this.todoWriteExample = todoWriteExample;
        this.subagentsExample = subagentsExample;
        this.autoMemoryExample = autoMemoryExample;
        this.sessionManagementExample = sessionManagementExample;
    }

    /**
     * Example 22: Chain Workflow (Prompt Chaining)
     * Endpoint to translate text, analyze sentiment, and draft an email response sequentially.
     */
    @PostMapping("/chain")
    public ChainWorkflow.ChainResult runChainWorkflow(@RequestBody(required = false) ChainRequest request) {
        String text = (request != null && request.text() != null) ? request.text() : "我最近用你们的软件遇到了许多问题，无法正常登录，希望能尽快解决并退款。";
        return this.chainWorkflow.runWorkflow(text);
    }

    /**
     * Example 23: Parallelization Workflow
     * Endpoint to run independent grammar, SEO, and safety evaluations concurrently on the input text.
     */
    @PostMapping("/parallel")
    public ParallelWorkflow.ParallelReport runParallelWorkflow(@RequestBody(required = false) ParallelRequest request) {
        String text = (request != null && request.text() != null) ? request.text() : "Java 25 introduces amazing virtual threads and pattern matching enhancements! Click here to buy our software immediately!!!";
        return this.parallelWorkflow.runWorkflow(text);
    }

    /**
     * Example 24: Routing Workflow
     * Endpoint to categorize the query into Billing/Technical/General and answer using a specialized persona.
     */
    @PostMapping("/routing")
    public RoutingWorkflow.RoutingResult runRoutingWorkflow(@RequestBody(required = false) RoutingRequest request) {
        String query = (request != null && request.query() != null) ? request.query() : "I got charged twice for this month's billing cycle, please refund me.";
        return this.routingWorkflow.runWorkflow(query);
    }

    /**
     * Example 25: Orchestrator-Workers Workflow
     * Endpoint to decompose a complex topic analysis, distribute sub-analysis to workers, and compile an executive report.
     */
    @PostMapping("/orchestrator")
    public OrchestratorWorkersWorkflow.OrchestratorResult runOrchestratorWorkflow(@RequestBody(required = false) OrchestratorRequest request) {
        String topic = (request != null && request.topic() != null) ? request.topic() : "Model Context Protocol (MCP) in Enterprise AI Architecture";
        return this.orchestratorWorkersWorkflow.runWorkflow(topic);
    }

    /**
     * Example 26: Evaluator-Optimizer Workflow
     * Endpoint to generate and iteratively refine a rhyming poem about a coding topic.
     */
    @PostMapping("/evaluator")
    public EvaluatorOptimizerWorkflow.WorkflowResult runEvaluatorWorkflow(@RequestBody(required = false) EvaluatorRequest request) {
        String topic = (request != null && request.topic() != null) ? request.topic() : "Garbage Collection in Java";
        return this.evaluatorOptimizerWorkflow.runWorkflow(topic);
    }

    /**
     * Example 27: Model Context Protocol (MCP) Integration
     * Endpoint demonstrating the conceptual and programmatic setup of an MCP filesystem server integration.
     */
    @PostMapping("/mcp")
    public McpWorkflow.McpDemoResult runMcpWorkflow(@RequestBody(required = false) McpRequest request) {
        String query = (request != null && request.query() != null) ? request.query() : "List files";
        return this.mcpWorkflow.runWorkflow(query);
    }

    /**
     * Example 28: Agent Skills
     * Endpoint to load modular capability definitions from SKILL.md dynamically.
     */
    @PostMapping("/skills")
    public String runSkillsExample(@RequestBody(required = false) SkillsRequest request) {
        String message = (request != null && request.message() != null) ? request.message() : "Please review this java code: public class Test { public static void main(String[] args) { System.out.println(\"Hello\"); } }";
        return this.skillsExample.runExample(message);
    }

    /**
     * Example 29: AskUserQuestion (Interactive Clarification)
     * Endpoint to prompt the user with choices if clarification is needed.
     */
    @PostMapping("/ask")
    public String runAskExample(@RequestBody(required = false) AskRequest request) {
        String message = (request != null && request.message() != null) ? request.message() : "What is the best way to deploy a spring boot app?";
        return this.askUserQuestionExample.runExample(message);
    }

    /**
     * Example 30: TodoWrite (Structured Task Planning)
     * Endpoint demonstrating structured progress updates for complex operations.
     */
    @PostMapping("/todo")
    public TodoWriteExample.TodoExampleResult runTodoExample(@RequestBody(required = false) TodoRequest request) {
        String message = (request != null && request.message() != null) ? request.message() : "Create a todo checklist for building a house.";
        String sessionId = (request != null && request.sessionId() != null) ? request.sessionId() : "todo-default-session";
        return this.todoWriteExample.runExample(message, sessionId);
    }

    /**
     * Example 31: Subagent Orchestration
     * Endpoint where a main orchestrator delegates tasks to specialized subagents.
     */
    @PostMapping("/subagents")
    public String runSubagentsExample(@RequestBody(required = false) SubagentsRequest request) {
        String message = (request != null && request.message() != null) ? request.message() : "Please perform a code review on our code quality using our refactoring subagent.";
        return this.subagentsExample.runExample(message);
    }

    /**
     * Example 32: AutoMemoryTools (Durable Long-Term Memory)
     * Endpoint to persist memory files across sessions.
     */
    @PostMapping("/memory")
    public String runMemoryExample(@RequestBody(required = false) MemoryRequest request) {
        String message = (request != null && request.message() != null) ? request.message() : "Remember that my favorite programming language is Java. What is my favorite programming language?";
        String sessionId = (request != null && request.sessionId() != null) ? request.sessionId() : "memory-default-session";
        return this.autoMemoryExample.runExample(message, sessionId);
    }

    /**
     * Example 33: Session Management
     * Endpoint demonstrating Turn-Aware Compaction with Session Service.
     */
    @PostMapping("/session")
    public String runSessionExample(@RequestBody(required = false) SessionRequest request) {
        String message = (request != null && request.message() != null) ? request.message() : "Hello, how are you?";
        String sessionId = (request != null && request.sessionId() != null) ? request.sessionId() : "default-session-id";
        return this.sessionManagementExample.runExample(message, sessionId);
    }

    public record ChainRequest(String text) {
    }

    public record ParallelRequest(String text) {
    }

    public record RoutingRequest(String query) {
    }

    public record OrchestratorRequest(String topic) {
    }

    public record EvaluatorRequest(String topic) {
    }

    public record McpRequest(String query) {
    }

    public record SkillsRequest(String message) {}

    public record AskRequest(String message) {}

    public record TodoRequest(String message, String sessionId) {}

    public record SubagentsRequest(String message) {}

    public record MemoryRequest(String message, String sessionId) {}

    public record SessionRequest(String message, String sessionId) {}
}
