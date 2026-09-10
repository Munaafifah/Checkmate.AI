package com.checkmate.backend.controller;

import com.checkmate.backend.dto.AnalyzeRequest;
import com.checkmate.backend.dto.AnalyzeResponse;
import com.checkmate.backend.dto.FitScore;
import com.checkmate.backend.service.GroqService;
import com.checkmate.backend.service.MlScoringService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyzeController {

    private final GroqService groqService;
    private final MlScoringService mlScoringService;

    public AnalyzeController(GroqService groqService, MlScoringService mlScoringService) {
        this.groqService = groqService;
        this.mlScoringService = mlScoringService;
    }

    @PostMapping("/api/analyze")
    public AnalyzeResponse analyze(@RequestBody AnalyzeRequest request) {
        if (request.resumeText() == null || request.resumeText().isBlank()) {
            throw new IllegalArgumentException("Resume text is required.");
        }
        if (request.jobDescription() == null || request.jobDescription().isBlank()) {
            throw new IllegalArgumentException("Job description is required.");
        }
        FitScore fitScore = mlScoringService.scoreOrNull(request.resumeText(), request.jobDescription());
        return groqService.generateQuestions(request.resumeText(), request.jobDescription(), fitScore);
    }
}
