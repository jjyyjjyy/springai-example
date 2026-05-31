package me.jy.example.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Retrieval-Augmented Generation (RAG) components.
 */
@Configuration
public class RagConfiguration {

    /**
     * Define the VectorStore bean.
     * SimpleVectorStore is an in-memory vector store suitable for development, testing,
     * or minimal search applications. It uses the configured Google GenAI EmbeddingModel
     * to compute embeddings for ingested documents and search queries.
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
