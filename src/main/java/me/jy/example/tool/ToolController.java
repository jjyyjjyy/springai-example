package me.jy.example.tool;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.function.Function;

/**
 * Category 4: Function Calling & Tools
 * Demonstrates how to equip Gemini with custom Java functions so it can retrieve
 * real-time, dynamic, or database-driven information.
 */
@RestController
@RequestMapping("/api/tool")
public class ToolController {

    private final ChatClient chatClient;
    private final WeatherService weatherService;
    private final Function<ToolConfiguration.StockRequest, ToolConfiguration.StockResponse> stockPriceFunction;

    public ToolController(ChatClient.Builder chatClientBuilder,
                          WeatherService weatherService,
                          Function<ToolConfiguration.StockRequest, ToolConfiguration.StockResponse> stockPriceFunction) {
        this.chatClient = chatClientBuilder.clone().build();
        this.weatherService = weatherService;
        this.stockPriceFunction = stockPriceFunction;
    }

    /**
     * Example 11: Tool via @Tool Annotation
     * Passes the weatherService bean directly to ChatClient's tools() method.
     * Spring AI scans its annotated methods and register them.
     */
    @PostMapping("/weather")
    public String weatherTool(@RequestBody(required = false) WeatherRequest request) {
        String city = (request != null && request.city() != null) ? request.city() : "Beijing";
        return this.chatClient.prompt()
            .user("What is the weather like in " + city + "?")
            .tools(tools -> tools.instances(this.weatherService)) // Register weatherService tool methods
            .call()
            .content();
    }

    /**
     * Example 12: Tool via @Bean Function Name
     * Registers the stock price function by pointing to its Spring bean name ("stockPriceFunction").
     * The model will automatically trigger it when the prompt asks about stock prices.
     */
    @PostMapping("/stock")
    public String stockTool(@RequestBody(required = false) StockRequest request) {
        String ticker = (request != null && request.ticker() != null) ? request.ticker() : "AAPL";

        var stockPriceCallback = FunctionToolCallback.builder("stockPriceFunction", stockPriceFunction)
            .description("Get the current stock price and currency for a given stock ticker symbol (e.g. AAPL, GOOG).")
            .inputType(ToolConfiguration.StockRequest.class)
            .build();

        return this.chatClient.prompt()
            .user("What is the current stock price of " + ticker + "?")
            .tools(tools -> tools.callbacks(stockPriceCallback)) // Reference function bean callback
            .call()
            .content();
    }

    /**
     * Example 13: Tool with ToolContext
     * Passes user-specific session context (e.g. username and tier) programmatically
     * to the weather service tool via .toolContext().
     */
    @PostMapping("/personalized")
    public String personalizedTool(@RequestBody(required = false) PersonalizedRequest request) {
        String username = (request != null && request.username() != null) ? request.username() : "Alice";
        String tier = (request != null && request.tier() != null) ? request.tier() : "Gold";

        return this.chatClient.prompt()
            .user("Get my personalized greeting message.")
            .tools(tools -> tools.instances(this.weatherService)
                .context(Map.of("username", username, "tier", tier))) // Inject custom context map
            .call()
            .content();
    }

    /**
     * Example 14: Dynamic Programmatic Tool
     * Programmatically constructs a FunctionToolCallback using its builder.
     * This is useful when tools are loaded dynamically or configured at runtime.
     */
    @PostMapping("/dynamic")
    public String dynamicTool(@RequestBody(required = false) DynamicToolRequest request) {
        double valA = (request != null && request.a() != null) ? request.a() : 153.25;
        double valB = (request != null && request.b() != null) ? request.b() : 846.75;

        // Build the tool callback programmatically
        var addTool = FunctionToolCallback.builder("addNumbers", (AddRequest req) -> new AddResponse(req.a() + req.b()))
            .description("Adds two floating-point numbers together and returns the sum.")
            .inputType(AddRequest.class)
            .build();

        return this.chatClient.prompt()
            .user(String.format("Please calculate the sum of %f and %f.", valA, valB))
            .tools(tools -> tools.callbacks(addTool)) // Register the dynamic tool callback
            .call()
            .content();
    }

    public record WeatherRequest(String city) {
    }

    public record StockRequest(String ticker) {
    }

    public record PersonalizedRequest(String username, String tier) {
    }

    public record DynamicToolRequest(Double a, Double b) {
    }

    // Records for dynamic tool input/output
    public record AddRequest(double a, double b) {
    }

    public record AddResponse(double result) {
    }
}
