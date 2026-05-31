package me.jy.example.agent;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Category 7: Agentic Patterns & Workflows REST Controller
 * Exposes endpoints to trigger and inspect intermediate execution steps
 * for each of the 6 agentic workflow patterns.
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

    public AgentController(ChainWorkflow chainWorkflow,
                           ParallelWorkflow parallelWorkflow,
                           RoutingWorkflow routingWorkflow,
                           OrchestratorWorkersWorkflow orchestratorWorkersWorkflow,
                           EvaluatorOptimizerWorkflow evaluatorOptimizerWorkflow,
                           McpWorkflow mcpWorkflow) {
        this.chainWorkflow = chainWorkflow;
        this.parallelWorkflow = parallelWorkflow;
        this.routingWorkflow = routingWorkflow;
        this.orchestratorWorkersWorkflow = orchestratorWorkersWorkflow;
        this.evaluatorOptimizerWorkflow = evaluatorOptimizerWorkflow;
        this.mcpWorkflow = mcpWorkflow;
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
}
