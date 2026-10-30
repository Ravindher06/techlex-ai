package com.example.techlexai;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/editor")
public class GlossaryController {
    private final GlossaryService glossaryService;

    public GlossaryController(GlossaryService glossaryService) {
        this.glossaryService = glossaryService;
    }

    @PostMapping(value = "/markdown", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> generateDocumentation(@Valid @RequestBody TextRequest request) {
        String markdown = glossaryService.cleanAndGenerateGlossary(request.rawText());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/markdown; charset=UTF-8")).body(markdown);
    }
}
