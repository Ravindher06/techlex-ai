package com.example.techlexai;

/** A small, curated glossary entry used as retrieval context for the LLM. */
public record GlossaryTerm(String name, String definition, String... keywords) {
    public boolean matches(String normalizedText) {
        for (String keyword : keywords) {
            if (normalizedText.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return normalizedText.contains(name.toLowerCase());
    }
}
