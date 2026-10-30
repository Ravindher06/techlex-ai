# TechLex AI — Intelligent Technical Glossary Assistant

TechLex AI is a Spring Boot REST API that transforms rough technical notes into clean, structured Markdown documentation. It uses a locally hosted **Ollama** language model to rewrite and organize the content, while a small curated glossary provides relevant technical definitions as additional context.

The project is designed as a lightweight example of integrating a local LLM into a Java backend without depending on a paid cloud AI API.

## Features

- Converts unstructured technical notes into readable Markdown.
- Uses a locally running Ollama model for text generation.
- Retrieves matching definitions from a curated technical glossary.
- Supports terms such as Dependency Injection, Idempotency, Garbage Collection, Lazy Loading, REST APIs, Microservices, Caching, Database Indexes, and Unit Testing.
- Validates incoming requests and rejects blank or oversized text.
- Returns Markdown content from a REST endpoint.
- Includes controller tests using Spring Boot Test and MockMvc.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Backend programming language |
| Spring Boot 3.4.3 | REST API and application framework |
| Spring AI 1.0.0 | Integration with the Ollama chat model |
| Ollama | Local LLM runtime |
| Llama 3.2 | Default language model |
| Maven | Dependency management and build |
| JUnit 5 / MockMvc | API testing |

## How It Works

1. A client sends rough technical notes to the Markdown generation endpoint.
2. The controller validates the request.
3. The glossary service searches the input for matching curated technical terms.
4. Matching definitions are added to the prompt as context.
5. Spring AI sends the prompt to the configured Ollama model.
6. The generated Markdown document is returned to the client.

```text
Client (Postman / Frontend)
          |
          | POST /api/v1/editor/markdown
          v
   GlossaryController
          |
          | Validate request
          v
     GlossaryService <------ Curated Glossary
          |
          | Build prompt + matched definitions
          v
       Spring AI
          |
          v
   Local Ollama Model
          |
          | Generated Markdown
          v
        Client
```

## Project Structure

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
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/example/techlexai/
            └── GlossaryControllerTest.java
```

## Prerequisites

Install the following before running the application:

- **JDK 17 or later**
- **Maven 3.9+**
- **Ollama** — [Download Ollama](https://ollama.com/download)

Check that Java and Maven are available:

```bash
java -version
mvn -version
```

## Setup and Run

### 1. Download the Ollama model

Open a terminal and run:

```bash
ollama pull llama3.2
```

### 2. Start Ollama

Ollama normally runs as a background service after installation. If it is not already running, start it with:

```bash
ollama serve
```

Keep Ollama running. Its default local address is:

```text
http://localhost:11434
```

If Ollama reports that the port is already in use, it may already be running; you do not need to start a second instance.

### 3. Clone the repository

```bash
git clone https://github.com/<your-username>/techlex-ai.git
cd techlex-ai
```

Replace `<your-username>` with your GitHub username and adjust the repository name if necessary.

### 4. Build and test

```bash
mvn clean test
```

### 5. Run the application

```bash
mvn spring-boot:run
```

The API will start at:

```text
http://localhost:8080
```

You can also import the project as a Maven project in IntelliJ IDEA, Eclipse, or VS Code and run `TechLexAiApplication.java`.

## API Documentation

### Generate Markdown

**Endpoint**

```http
POST http://localhost:8080/api/v1/editor/markdown
```

**Headers**

```http
Content-Type: application/json
```

**Request body**

```json
{
  "rawText": "The current architecture has high coupling issues. We need dependency injection to pass dependencies externally. Heavy user profile objects are loaded immediately, slowing startup, so we should switch to lazy loading."
}
```

**Successful response**

- Status: `200 OK`
- Content type: `text/markdown`
- Body: A generated Markdown document containing an appropriate title, organized sections, and relevant technical explanations. The exact wording can vary between model runs.

Example of the kind of Markdown structure the model may return:

```markdown
# Improving Application Architecture

## Overview
The current architecture has high coupling and startup-performance concerns.

## Recommended Improvements
- Apply Dependency Injection to reduce coupling.
- Use Lazy Loading to defer initialization of resources until required.

## Technical Glossary
- **Dependency Injection (DI):** ...
- **Lazy Loading:** ...
```

The example above illustrates the expected structure; it is not a guaranteed, fixed model response.

### Validation error example

Send a blank value:

```json
{
  "rawText": "   "
}
```

Expected result:

- Status: `400 Bad Request`
- Response body:

```json
{
  "error": "rawText must not be blank"
}
```

The `rawText` field is limited to 20,000 characters.

### Test with cURL

```bash
curl -X POST http://localhost:8080/api/v1/editor/markdown \
  -H "Content-Type: application/json" \
  -d "{\"rawText\":\"Explain dependency injection and lazy loading in a Spring Boot application.\"}"
```

You can also send the same request using Postman by selecting **POST**, entering the endpoint, and choosing **Body → raw → JSON**.

## Configuration

The default settings are in:

```text
src/main/resources/application.properties
```

```properties
spring.application.name=techlex-ai
server.port=8080

spring.ai.ollama.base-url=${OLLAMA_BASE_URL:http://localhost:11434}
spring.ai.ollama.chat.options.model=${OLLAMA_CHAT_MODEL:llama3.2}
spring.ai.ollama.chat.options.temperature=0.2
```

To use a different Ollama host or model, set environment variables before starting the application.

**Windows PowerShell:**

```powershell
$env:OLLAMA_BASE_URL="http://localhost:11434"
$env:OLLAMA_CHAT_MODEL="llama3.2"
mvn spring-boot:run
```

**Linux / macOS:**

```bash
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_CHAT_MODEL=llama3.2
mvn spring-boot:run
```

Make sure the selected model has been downloaded in Ollama.

## Running Tests

Run the test suite with:

```bash
mvn test
```

The included controller tests verify that:
- A valid request returns generated Markdown with `200 OK`.
- A blank request is rejected with `400 Bad Request`.

The controller test mocks the glossary service, so it does not require Ollama to be running.

## Current Scope and Limitations

- The glossary is a small, curated in-memory list. It resets when the application restarts.
- Glossary retrieval uses keyword matching, not embeddings or a vector database.
- Generated content depends on the configured local model; wording and formatting may vary.
- Ollama must be running and the configured model must be available when a generation request is made.
- This project currently provides a backend REST API; it does not include a frontend or persistent database.

## Possible Future Enhancements

- Add a frontend editor with Markdown preview.
- Expand the glossary and support user-managed glossary entries.
- Add semantic retrieval using embeddings and a vector database.
- Store generated documents and glossary entries in PostgreSQL.
- Add authentication, request logging, and rate limiting.
- Add integration tests that run against a configured Ollama instance.

## Contributing

Contributions and suggestions are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Make and test your changes.
4. Submit a pull request with a clear description.
