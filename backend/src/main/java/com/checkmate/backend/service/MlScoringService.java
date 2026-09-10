package com.checkmate.backend.service;

import com.checkmate.backend.dto.FitScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MlScoringService {

    private static final Logger log = LoggerFactory.getLogger(MlScoringService.class);

    private final RestClient mlServiceRestClient;

    public MlScoringService(RestClient mlServiceRestClient) {
        this.mlServiceRestClient = mlServiceRestClient;
    }

    // Returns null instead of throwing if the ML service is unreachable, so
    // analysis still works (minus the score) when the Python service is down.
    public FitScore scoreOrNull(String resumeText, String jobDescription) {
        try {
            return mlServiceRestClient.post()
                    .uri("/score")
                    .body(new ScoreRequest(resumeText, jobDescription))
                    .retrieve()
                    .body(FitScore.class);
        } catch (Exception e) {
            log.warn("ML scoring service call failed, continuing without a fit score", e);
            return null;
        }
    }

    private record ScoreRequest(String resumeText, String jobDescription) {
    }
}
