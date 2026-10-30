package com.example.techlexai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(GlossaryController.class)
@Import(ApiExceptionHandler.class)
class GlossaryControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean GlossaryService glossaryService;

    @Test
    void returnsGeneratedMarkdown() throws Exception {
        when(glossaryService.cleanAndGenerateGlossary(anyString())).thenReturn("# Clean notes");
        mockMvc.perform(post("/api/v1/editor/markdown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rawText\":\"dependency injection helps testing\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("# Clean notes"));
    }

    @Test
    void rejectsBlankText() throws Exception {
        mockMvc.perform(post("/api/v1/editor/markdown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rawText\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }
}
