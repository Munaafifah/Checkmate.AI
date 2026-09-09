package com.checkmate.backend.controller;

import com.checkmate.backend.dto.ResumeUploadResponse;
import com.checkmate.backend.service.ResumeParsingService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ResumeController {

    private final ResumeParsingService resumeParsingService;

    public ResumeController(ResumeParsingService resumeParsingService) {
        this.resumeParsingService = resumeParsingService;
    }

    @PostMapping(value = "/api/resume/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResumeUploadResponse upload(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "text", required = false) String text) {

        String resumeText;
        if (file != null && !file.isEmpty()) {
            resumeText = resumeParsingService.extractTextFromPdf(file);
        } else if (text != null && !text.isBlank()) {
            resumeText = text.trim();
        } else {
            throw new IllegalArgumentException("Provide either a PDF file or pasted resume text.");
        }

        return new ResumeUploadResponse(resumeText, resumeText.length());
    }
}
