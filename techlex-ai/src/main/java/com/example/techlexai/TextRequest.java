package com.example.techlexai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TextRequest(
        @NotBlank(message = "rawText must not be blank")
        @Size(max = 20000, message = "rawText must be 20,000 characters or fewer")
        String rawText) {
}
