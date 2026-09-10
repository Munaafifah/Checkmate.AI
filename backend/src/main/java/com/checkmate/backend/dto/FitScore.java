package com.checkmate.backend.dto;

import java.util.List;

public record FitScore(
        double overallScore,
        Integer yearsOfExperience,
        List<RequirementMatch> matchedRequirements,
        List<RequirementMatch> missingRequirements) {
}
