package com.checkmate.backend.dto;

import java.util.List;

public record AnalyzeResponse(String summary, List<InterviewQuestion> questions) {
}
