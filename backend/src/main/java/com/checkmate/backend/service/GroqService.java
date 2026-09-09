package com.checkmate.backend.service;

import com.checkmate.backend.dto.AnalyzeResponse;
import com.checkmate.backend.exception.GroqApiException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class GroqService {

    private static final Logger log = LoggerFactory.getLogger(GroqService.class);

    private static final String SYSTEM_PROMPT = """
            You are an expert technical interviewer and career coach.

            You will be given a candidate's resume text and a target job description. Do the following:
            1. Identify the candidate's strongest matching areas (skills/experience that align well with the job description).
            2. Identify the candidate's weakest gaps (job requirements not well evidenced in the resume).
            3. Write a 2-4 sentence fit summary covering both.
            4. Generate exactly 5 technical interview questions that probe the identified gaps.
            5. Generate exactly 5 behavioral interview questions that explore the identified strengths.

            Respond with ONLY valid JSON matching this exact schema, no markdown code fences, no commentary:
            {
              "summary": "string",
              "questions": [
                {"type": "technical", "question": "string"},
                {"type": "behavioral", "question": "string"}
              ]
            }
            The "questions" array must contain exactly 5 items with type "technical" and exactly 5 with type "behavioral".
            """;

    private final RestClient groqRestClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public GroqService(RestClient groqRestClient, ObjectMapper objectMapper,
                        @Value("${groq.api.model}") String model) {
        this.groqRestClient = groqRestClient;
        this.objectMapper = objectMapper;
        this.model = model;
    }

    public AnalyzeResponse generateQuestions(String resumeText, String jobDescription) {
        String userPrompt = "RESUME:\n" + resumeText + "\n\nJOB DESCRIPTION:\n" + jobDescription;

        String content = callGroq(userPrompt);
        try {
            return objectMapper.readValue(content, AnalyzeResponse.class);
        } catch (Exception firstFailure) {
            log.warn("Groq response was not valid JSON on first attempt, retrying with a stricter reminder");
            String retryPrompt = userPrompt +
                    "\n\nYour previous reply was not valid JSON. Reply with ONLY valid JSON, no markdown, no commentary.";
            String retryContent = callGroq(retryPrompt);
            try {
                return objectMapper.readValue(retryContent, AnalyzeResponse.class);
            } catch (Exception secondFailure) {
                throw new GroqApiException(
                        "The AI response could not be parsed. Please try again.", secondFailure);
            }
        }
    }

    private String callGroq(String userPrompt) {
        ChatCompletionRequest request = new ChatCompletionRequest(
                model,
                List.of(
                        new ChatMessage("system", SYSTEM_PROMPT),
                        new ChatMessage("user", userPrompt)
                ),
                new ResponseFormat("json_object"),
                0.4
        );

        try {
            ChatCompletionResponse response = groqRestClient.post()
                    .uri("/chat/completions")
                    .body(request)
                    .retrieve()
                    .body(ChatCompletionResponse.class);

            if (response == null || response.choices() == null || response.choices().isEmpty()) {
                throw new GroqApiException("Groq returned an empty response.");
            }
            return response.choices().get(0).message().content();
        } catch (GroqApiException e) {
            throw e;
        } catch (Exception e) {
            throw new GroqApiException("Failed to reach the Groq API. Please try again shortly.", e);
        }
    }

    private record ChatCompletionRequest(
            String model,
            List<ChatMessage> messages,
            @JsonProperty("response_format") ResponseFormat responseFormat,
            Double temperature) {
    }

    private record ResponseFormat(String type) {
    }

    private record ChatMessage(String role, String content) {
    }

    private record ChatCompletionResponse(List<Choice> choices) {
    }

    private record Choice(ChatMessage message) {
    }
}
