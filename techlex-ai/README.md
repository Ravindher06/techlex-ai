# TechLex AI — Intelligent Technical Glossary Assistant

Spring Boot REST API that converts rough technical notes into structured Markdown using a locally running Ollama model. It retrieves matching definitions from a small in-memory curated glossary and supplies them as context to the model.

## Requirements
- JDK 17 or newer
- Maven 3.9+
- Ollama installed and running locally
- The `llama3.2` model downloaded

## Run on Windows PowerShell

```powershell
ollama pull llama3.2
# Keep Ollama running (Ollama Desktop normally starts its local service).
ollama serve
```

In a second PowerShell window, from the folder containing `pom.xml`:

```powershell
mvn clean test
mvn spring-boot:run
```

If Ollama is already running, `ollama serve` may report that the port is in use; that is okay. Alternatively, start the app from your IDE by running `TechLexAiApplication.java`.

## API test

POST `http://localhost:8080/api/v1/editor/markdown`

Header: `Content-Type: application/json`

Body:

```json
{
  "rawText": "The current architecture has high coupling issues. We need dependency injection to pass dependencies externally. Heavy user profile objects are loaded immediately, slowing startup, so we should switch to lazy loading."
}
```

Expected response: HTTP `200 OK`, content type `text/markdown`, and a Markdown document with a title, summary/action points, and curated definitions for Dependency Injection and Lazy Loading. Exact prose varies by model, so output will not be byte-for-byte identical every run.

Invalid input example:

```json
{"rawText":"   "}
```

Expected: HTTP `400 Bad Request` with a JSON error message.

## Configuration
Defaults are in `src/main/resources/application.properties`. You can override the Ollama URL and chat model using environment variables:

```powershell
$env:OLLAMA_BASE_URL="http://localhost:11434"
$env:OLLAMA_CHAT_MODEL="llama3.2"
mvn spring-boot:run
```

## Project layout

```text
techlex-ai/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/example/techlexai/
    │   │   ├── TechLexAiApplication.java
    │   │   ├── GlossaryController.java
    │   │   ├── GlossaryService.java
    │   │   ├── GlossaryTerm.java
    │   │   ├── TextRequest.java
    │   │   └── ApiExceptionHandler.java
    │   └── resources/application.properties
    └── test/java/com/example/techlexai/GlossaryControllerTest.java
```

## Notes and limitations
- The glossary is in memory and resets when the application restarts.
- Retrieval uses a small curated keyword-matching glossary, not an embedding-based vector database. This avoids requiring a separate embedding model for this small demo. It can be upgraded to a vector store if semantic retrieval is needed.
- Ollama must be reachable at runtime. If it is unavailable or the model is missing, generation returns HTTP 503.
- The Maven build has not been executed in this environment because Maven is not installed here. Run `mvn clean test` locally to verify dependency resolution and compilation.
