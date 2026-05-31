# Spring AI 2.0.0-SNAPSHOT & Google Gemini Examples

This project is a progressive, step-by-step example codebase demonstrating **Spring AI 2.0.0-SNAPSHOT** features using **Google Gemini** models. It covers 33 distinct examples ranging from basic chat prompts to structured output parsing,
multimodal analysis, custom tool calling (functions), conversation memory management, RAG (Retrieval-Augmented
Generation), advanced Agentic Workflow patterns, and Agent Community utilities/Memory advisors.

📬 **Postman Collection**: A pre-configured [postman.json](file:///Users/jy/IdeaProjects/springai-example/postman.json)
file is included in the project root. You can import it directly into Postman to test all 33 endpoints instantly!

---

## 🛠️ Technology Stack

- **Java 25**
- **Spring Boot 4.1.0-SNAPSHOT**
- **Spring AI 2.0.0-SNAPSHOT**
- **Google Gemini 3.1 Flash Lite**

---

## 🚀 Getting Started

### 1. Set up your Google Gemini API Key

To run this application, you need a Google Gemini API Key. You can get one
from [Google AI Studio](https://aistudio.google.com/).
Set it as an environment variable in your terminal:

```bash
export GEMINI_API_KEY="your-api-key-here"
```

### 2. Compile and Build

Ensure you are using Java 25. Run the Maven compile step:

```bash
./mvnw clean compile
```

### 3. Run Application

Start the backend server (ensure your `GEMINI_API_KEY` is exported):

```bash
export GEMINI_API_KEY="your-api-key-here"
./mvnw spring-boot:run
```

Once the application is running, open your web browser and navigate to:

```
http://localhost:8080/
```

This will load the **Interactive Playground Dashboard** (hosted in `src/main/resources/static/index.html`), allowing you to visually execute and test all 34 examples directly!

---

## 📚 Codebase Catalog & API endpoints (33 Examples)

Here is a list of all 33 progressive examples implemented across the controllers, together with the `curl` commands to
test them using JSON request bodies.

---

### Category 1: ChatClient Fluent API & Basics (`/api/chat`)

Implemented in `SimpleChatController.java`

* **Example 1: Basic Chat** - Simple prompt and string response.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"message": "Explain Spring AI in one sentence"}' http://localhost:8080/api/chat/simple
  ```
* **Example 2: Custom Options** - Dynamic runtime override for model temperature.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"message": "Write a haiku about Java 25", "temperature": 0.9}' http://localhost:8080/api/chat/options
  ```
* **Example 3: Streaming Chat** - Tokens streamed in real-time as Server-Sent Events (SSE).
  ```bash
  curl -N -X POST -H "Content-Type: application/json" -d '{"message": "Write a short essay on AI agents"}' http://localhost:8080/api/chat/stream
  ```
* **Example 4: Prompt Templates** - Rendering prompts dynamically with template parameters.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"language": "French", "text": "I love programming in Spring Boot!"}' http://localhost:8080/api/chat/template
  ```
* **Example 5: Message Roles** - Specifying system prompts to establish persona.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"message": "IDE recommendation", "systemPersona": "You are a grumpy COBOL programmer"}' http://localhost:8080/api/chat/roles
  ```

---

### Category 2: Structured Output Converters (`/api/structured`)

Implemented in `StructuredController.java`

* **Example 6: Bean Output Converter** - Parses response directly into a custom Java `record`.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"bookName": "The Hobbit"}' http://localhost:8080/api/structured/bean
  ```
* **Example 7: Map Output Converter** - Maps responses to a dynamic `Map<String, Object>`.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"companyName": "Apple"}' http://localhost:8080/api/structured/map
  ```
* **Example 8: List Output Converter** - Returns a clean `List<String>` of items.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"domain": "frontend"}' http://localhost:8080/api/structured/list
  ```

---

### Category 3: Multimodality / Vision & Audio (`/api/multimodal`)

Implemented in `MultimodalController.java`

* **Example 9: Image Analysis (Vision)** - Describe the visual composition of an image URL.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"imageUrl": "https://images.unsplash.com/photo-1579546929518-9e396f3cc809", "prompt": "Describe the colors in this image"}' http://localhost:8080/api/multimodal/image
  ```
* **Example 10: Audio Description** - Analyze an audio clip for instruments, tempo, and vibe.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"audioUrl": "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "prompt": "Describe the vibe of this music"}' http://localhost:8080/api/multimodal/audio
  ```

---

### Category 4: Function Calling & Tools (`/api/tool`)

Implemented in `ToolController.java`, `WeatherService.java` & `ToolConfiguration.java`

* **Example 11: Tool Annotation** - Automatically detects methods annotated with `@Tool`.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"city": "Tokyo"}' http://localhost:8080/api/tool/weather
  ```
* **Example 12: Bean Functions** - Invokes a standard Java functional `@Bean` by name.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"ticker": "AAPL"}' http://localhost:8080/api/tool/stock
  ```
* **Example 13: Tool Context** - Passes request-scoped metadata (username, tier) to tool execution via `ToolContext`.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"username": "Alice", "tier": "Gold"}' http://localhost:8080/api/tool/personalized
  ```
* **Example 14: Dynamic Programmatic Tool** - Builds a `FunctionToolCallback` programmatically at runtime.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"a": 12.5, "b": 87.5}' http://localhost:8080/api/tool/dynamic
  ```

---

### Category 5: Chat Memory & Conversational Advisors (`/api/memory`)

Implemented in `MemoryController.java`

* **Example 15: Message Chat Memory** - Maintains conversation history as a list of messages.
  ```bash
  # Step A: Introduce yourself
  curl -X POST -H "Content-Type: application/json" -d '{"sessionId": "session123", "message": "Hi, my name is Bob"}' http://localhost:8080/api/memory/message
  # Step B: Ask who you are
  curl -X POST -H "Content-Type: application/json" -d '{"sessionId": "session123", "message": "What is my name?"}' http://localhost:8080/api/memory/message
  ```
* **Example 16: Vector Store Memory** - Semantically retrieves relevant historical context from a VectorStore.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"sessionId": "session123", "message": "Tell me a fact about space"}' http://localhost:8080/api/memory/vector
  ```
* **Example 17: Raw Memory Inspection & Operations** - View or clear the stored in-memory chat history.
  ```bash
  # Get raw history
  curl -X POST -H "Content-Type: application/json" -d '{"sessionId": "session123"}' http://localhost:8080/api/memory/history
  # Clear memory
  curl -X POST -H "Content-Type: application/json" -d '{"sessionId": "session123"}' http://localhost:8080/api/memory/clear
  ```

---

### Category 6: Embeddings, Vector Store & RAG (`/api/rag`)

Implemented in `RagController.java` & `RagConfiguration.java`

* **Example 18: Manual Embeddings** - Generate a high-dimensional vector representation of text.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"text": "Hello World"}' http://localhost:8080/api/rag/embed
  ```
* **Example 19: Ingestion Pipeline** - Splitting documents via `TokenTextSplitter` and embedding them. (Remains GET
  since it is parameter-less)
  ```bash
  curl http://localhost:8080/api/rag/ingest-mock
  ```
* **Example 20: Simple QA RAG Advisor** - Ask questions answered using context from the `VectorStore`.
  *(Note: Run ingest-mock first to store the sample documents!)*
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"question": "What is Antigravity?"}' http://localhost:8080/api/rag/ask
  ```
* **Example 21: Vector Search with Filters** - Match vectors constrained by category metadata filters.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"query": "AI model", "filterExpression": "category == '\''gemini'\''"}' http://localhost:8080/api/rag/search
  ```

---

### Category 7: Agentic Patterns & Workflows (`/api/agent`)

Implemented in `me.jy.example.agent` package.

* **Example 22: Chain Workflow (Prompt Chaining)** - Sequential execution: Translate -> Analyze Sentiment -> Draft
  response.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"text": "我遇到了一些技术故障，希望退款。"}' http://localhost:8080/api/agent/chain
  ```
* **Example 23: Parallelization** - Runs grammar, SEO, and policy checks concurrently and merges reports.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"text": "This is great buy now fast!!!"}' http://localhost:8080/api/agent/parallel
  ```
* **Example 24: Routing** - Directs ticket to Billing/Technical specialist persona dynamically based on classification.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"query": "My login is failing"}' http://localhost:8080/api/agent/routing
  ```
* **Example 25: Orchestrator-Workers** - Central LLM distributes SWOT, Competitor, and Trend analysis to workers,
  compiles final report.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"topic": "Autonomous Vehicles"}' http://localhost:8080/api/agent/orchestrator
  ```
* **Example 26: Evaluator-Optimizer** - Iteratively refines content (a poem) based on scoring and feedback until quality
  threshold is met.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"topic": "Java Streams"}' http://localhost:8080/api/agent/evaluator
  ```
* **Example 27: Model Context Protocol (MCP)** - Conceptually maps local/remote server tools to the `ChatClient`.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"query": "List files"}' http://localhost:8080/api/agent/mcp
  ```
* **Example 28: Agent Skills** - Modular capabilities loaded dynamically from markdown instructions.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"message": "Please review this java code: public class Test { public static void main(String[] args) { System.out.println(\"Hello\"); } }"}' http://localhost:8080/api/agent/skills
  ```
* **Example 29: AskUserQuestion (Interactive Clarification)** - Structured multiple-choice clarification prompt to the user.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"message": "What is the best way to deploy a spring boot app?"}' http://localhost:8080/api/agent/ask
  ```
* **Example 30: TodoWrite (Structured Task Planning)** - Step-by-step sequential planning and progress events during complex execution.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"message": "Create a todo checklist for building a house.", "sessionId": "todo-session-123"}' http://localhost:8080/api/agent/todo
  ```
* **Example 31: Subagent Orchestration** - Main orchestrator delegates tasks to specialized subagents in isolated context windows.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"message": "Please perform a code review on our code quality using our refactoring subagent."}' http://localhost:8080/api/agent/subagents
  ```
* **Example 32: AutoMemoryTools (Durable Long-Term Memory)** - Long-term facts persisted in structured files across multiple sessions.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"message": "Remember that my favorite programming language is Java. What is my favorite programming language?", "sessionId": "memory-session-123"}' http://localhost:8080/api/agent/memory
  ```
* **Example 33: Session Management (Turn-Aware Compaction)** - Turn-aware conversation event sourcing and compaction triggers.
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"message": "Hello, how are you?", "sessionId": "session-999"}' http://localhost:8080/api/agent/session
  ```
