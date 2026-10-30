package com.example.techlexai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class GlossaryService {
    private static final List<GlossaryTerm> TERMS = List.of(
            new GlossaryTerm("Dependency Injection (DI)",
                    "A design technique in which an object receives the dependencies it needs from an external component rather than constructing them itself.",
                    "dependency injection", "dependency-injection", "inversion of control", "ioc"),
            new GlossaryTerm("Idempotency",
                    "A property of an operation where repeating the same request has the same intended effect as performing it once.",
                    "idempotent", "idempotency", "retry-safe"),
            new GlossaryTerm("Garbage Collection (GC)",
                    "Automatic memory management that reclaims memory occupied by objects that are no longer reachable.",
                    "garbage collection", "garbage collector", "memory reclamation"),
            new GlossaryTerm("Lazy Loading",
                    "A technique that postpones loading or initializing a resource until it is first needed.",
                    "lazy loading", "lazy-load", "load on demand"),
            new GlossaryTerm("REST API",
                    "An HTTP API designed around resource representations and stateless interactions, commonly using methods such as GET, POST, PUT, and DELETE.",
                    "rest api", "restful", "http endpoint"),
            new GlossaryTerm("Microservices",
                    "An architectural style in which an application is composed of small services that can be developed and deployed independently.",
                    "microservice", "microservices", "service boundaries"),
            new GlossaryTerm("Caching",
                    "Keeping copies of frequently used data in a faster storage layer to reduce repeated computation or slower data access.",
                    "cache", "caching", "redis"),
            new GlossaryTerm("Database Index",
                    "A data structure that helps a database locate rows faster, usually trading additional storage and write overhead for faster reads.",
                    "database index", "indexing", "query performance"),
            new GlossaryTerm("Unit Testing",
                    "Testing an isolated unit of code to check that it behaves as expected for selected inputs and conditions.",
                    "unit test", "unit testing", "junit", "mocking")
    );

    private final ChatClient chatClient;

    public GlossaryService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String cleanAndGenerateGlossary(String rawText) {
        String normalized = rawText.toLowerCase(Locale.ROOT);
        List<GlossaryTerm> matched = TERMS.stream()
                .filter(term -> term.matches(normalized))
                .limit(6)
                .toList();

        String context = matched.isEmpty()
                ? "No curated glossary terms matched the input. Do not invent official definitions; provide a useful document and omit the glossary appendix or state that no curated terms matched."
                : matched.stream()
                    .map(term -> "- **" + term.name() + "**: " + term.definition())
                    .collect(Collectors.joining("\n"));

        return chatClient.prompt()
                .system("""
                        You are a careful senior technical writer. Rewrite the user's technical notes as a clear, accurate Markdown document.
                        Preserve the user's meaning and do not invent project facts, metrics, APIs, or implementation details.
                        Use a concise title, meaningful headings, and bullets where useful. Correct grammar without changing technical intent.
                        The curated glossary definitions below are trusted context. If a glossary appendix is included, reproduce each selected definition accurately; do not claim they came from an external official source.
                        Return only the Markdown document, without code fences around the whole response.
                        Curated glossary context:
                        %s
                        """.formatted(context))
                .user("Format these notes as Markdown:\n\n" + rawText)
                .call()
                .content();
    }
}
