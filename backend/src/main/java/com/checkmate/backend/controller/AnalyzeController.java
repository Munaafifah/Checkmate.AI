package com.checkmate.backend.controller;

import com.checkmate.backend.dto.AnalyzeRequest;
import com.checkmate.backend.dto.AnalyzeResponse;
import com.checkmate.backend.service.GroqService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyzeController {

    private final GroqService groqService;

    public AnalyzeController(GroqService groqService) {
        this.groqService = groqService;
    }

    @PostMapping("/api/analyze")
    public AnalyzeResponse analyze(@RequestBody AnalyzeRequest request) {
        if (request.resumeText() == null || request.resumeText().isBlank()) {
            throw new IllegalArgumentException("Resume text is required.");
        }
        if (request.jobDescription() == null || request.jobDescription().isBlank()) {
            throw new IllegalArgumentException("Job description is required.");
        }
        return groqService.generateQuestions(request.resumeText(), request.jobDescription());
    }
}
