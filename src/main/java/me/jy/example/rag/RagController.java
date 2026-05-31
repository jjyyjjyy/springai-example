package me.jy.example.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Category 6: Embeddings, Vector Store & RAG
 * Demonstrates semantic search and Retrieval-Augmented Generation (RAG)
 * using Spring AI 2.0.0-SNAPSHOT and Google Gemini.
 */
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public RagController(ChatClient.Builder chatClientBuilder, EmbeddingModel embeddingModel, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.clone().build();
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }

    /**
     * Example 18: Manual Embedding Generation
     * Directly invokes the Google GenAI embedding model to convert text
     * into a high-dimensional vector (float array).
     */
    @PostMapping("/embed")
    public float[] embedText(@RequestBody(required = false) EmbedRequest request) {
        String txt = (request != null && request.text() != null) ? request.text() : "Spring AI makes semantic search simple.";
        return this.embeddingModel.embed(txt);
    }

    /**
     * Example 19: Ingestion Pipeline (Mock / Dynamic)
     * Ingests documents, splits them into token-friendly chunks using TokenTextSplitter,
     * computes their embeddings, and saves them to the SimpleVectorStore.
     */
    @GetMapping("/ingest-mock")
    public String ingestMockDocs() {
        List<Document> rawDocuments = List.of(
            new Document("Antigravity IDE is an advanced agentic AI coding assistant designed by Google DeepMind.",
                Map.of("category", "antigravity", "author", "deepmind")),
            new Document("Spring AI 2.0.0-SNAPSHOT brings native support for Model Context Protocol (MCP).",
                Map.of("category", "spring", "author", "spring")),
            new Document("Gemini 3.1 Flash lite is a lightweight, low-latency multimodal model developed by Google.",
                Map.of("category", "gemini", "author", "google")),
            new Document("The VectorStore abstraction allows storing text along with computed high-dimensional vector embeddings.",
                Map.of("category", "spring", "author", "spring"))
        );

        // Define a token text splitter to chunk documents (e.g. max 100 tokens per chunk)
        TokenTextSplitter splitter = TokenTextSplitter.builder().build();
        List<Document> splitDocuments = splitter.apply(rawDocuments);

        // Store document chunks in the vector store
        this.vectorStore.accept(splitDocuments);

        return String.format("Successfully split %d raw documents into %d chunks and ingested them into VectorStore.",
            rawDocuments.size(), splitDocuments.size());
    }

    /**
     * Example 20: Simple QA RAG Advisor
     * Uses QuestionAnswerAdvisor to perform RAG. When the user asks a question,
     * the advisor automatically queries the VectorStore, extracts relevant documents,
     * appends them to the prompt context, and retrieves a grounded response from Gemini.
     */
    @PostMapping("/ask")
    public String askRag(@RequestBody(required = false) AskRequest request) {
        String q = (request != null && request.question() != null) ? request.question() : "What is Antigravity?";
        return this.chatClient.prompt()
            .user(q)
            // Add the QuestionAnswerAdvisor to handle RAG automatically
            .advisors(QuestionAnswerAdvisor.builder(this.vectorStore).build())
            .call()
            .content();
    }

    /**
     * Example 21: Manual Vector Search with Metadata Filters
     * Performs a manual similarity search against the VectorStore.
     * Applies filter expressions to search only within specific metadata categories.
     */
    @PostMapping("/search")
    public List<String> searchWithFilters(@RequestBody(required = false) SearchRequestPayload request) {
        String q = (request != null && request.query() != null) ? request.query() : "AI model";
        String filter = (request != null && request.filterExpression() != null) ? request.filterExpression() : "category == 'gemini'";

        SearchRequest searchRequest = SearchRequest.builder()
            .query(q)
            .topK(3)
            .filterExpression(filter)
            .build();

        List<Document> matchingDocs = this.vectorStore.similaritySearch(searchRequest);

        return matchingDocs.stream()
            .map(doc -> String.format("[Score: %f][Category: %s] %s",
                (doc.getMetadata().containsKey("distance") ? doc.getMetadata().get("distance") : 0.0),
                doc.getMetadata().get("category"),
                doc.getText()))
            .toList();
    }

    public record EmbedRequest(String text) {
    }

    public record AskRequest(String question) {
    }

    public record SearchRequestPayload(String query, String filterExpression) {
    }
}
