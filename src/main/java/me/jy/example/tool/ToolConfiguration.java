package me.jy.example.tool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * Configuration class to register Spring Beans as tools for Spring AI.
 */
@Configuration
public class ToolConfiguration {

    /**
     * Example 12: Tool via @Bean Function
     * Registering a standard Java Function as a Spring bean.
     * The @Description annotation is critical because Spring AI uses it to tell the LLM when to call this function.
     */
    @Bean
    @Description("Get the current stock price and currency for a given stock ticker symbol (e.g. AAPL, GOOG).")
    public Function<StockRequest, StockResponse> stockPriceFunction() {
        return request -> {
            String ticker = request.ticker().toUpperCase();
            double mockPrice = switch (ticker) {
                case "AAPL" -> 180.50;
                case "GOOG" -> 175.20;
                case "MSFT" -> 420.10;
                default -> 100.00 + Math.random() * 50;
            };
            return new StockResponse(ticker, mockPrice, "USD");
        };
    }

    // Input payload for the tool
    public record StockRequest(String ticker) {
    }

    // Output payload from the tool
    public record StockResponse(String ticker, double price, String currency) {
    }
}
