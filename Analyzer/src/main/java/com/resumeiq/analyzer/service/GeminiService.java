package com.resumeiq.analyzer.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeiq.analyzer.dto.AnalyzeResponse;

@Service
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalyzeResponse analyzeResume(String resumeText, String jobDescription) {

        if (apiKey == null || apiKey.isBlank()) {
            return fallbackAnalysis(resumeText, jobDescription);
        }

        try {
            String prompt = buildPrompt(resumeText, jobDescription);

            String requestBody = """
                    {
                      "contents": [
                        {
                          "parts": [
                            {
                              "text": %s
                            }
                          ]
                        }
                      ]
                    }
                    """.formatted(objectMapper.writeValueAsString(prompt));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                                    + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());

            String aiText = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return parseAiResponse(aiText);

        } catch (Exception e) {
            return fallbackAnalysis(resumeText, jobDescription);
        }
    }

    private String buildPrompt(String resumeText, String jobDescription) {
        return """
                You are an expert ATS resume evaluator and technical recruiter.

                Analyze the resume against the given job description.

                Return ONLY valid JSON. No markdown. No explanation outside JSON.

                JSON format:
                {
                  "atsScore": 85,
                  "strengths": ["point 1", "point 2", "point 3"],
                  "weaknesses": ["point 1", "point 2"],
                  "missingSkills": ["skill 1", "skill 2"],
                  "suggestions": ["suggestion 1", "suggestion 2", "suggestion 3"],
                  "summary": "short recruiter-style summary"
                }

                Resume:
                %s

                Job Description:
                %s
                """.formatted(limitText(resumeText), limitText(jobDescription));
    }

    private AnalyzeResponse parseAiResponse(String aiText) {
        try {
            String cleaned = aiText
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonNode node = objectMapper.readTree(cleaned);

            return AnalyzeResponse.builder()
                    .atsScore(node.path("atsScore").asInt())
                    .strengths(toList(node.path("strengths")))
                    .weaknesses(toList(node.path("weaknesses")))
                    .missingSkills(toList(node.path("missingSkills")))
                    .suggestions(toList(node.path("suggestions")))
                    .summary(node.path("summary").asText())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response");
        }
    }

    private List<String> toList(JsonNode node) {
        if (node == null || !node.isArray()) {
            return List.of();
        }

        return objectMapper.convertValue(
                node,
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
    }

    private String limitText(String text) {
        if (text == null)
            return "";
        return text.length() > 12000 ? text.substring(0, 12000) : text;
    }

    private AnalyzeResponse fallbackAnalysis(String resumeText, String jobDescription) {

        String resume = resumeText.toLowerCase();
        String jd = jobDescription.toLowerCase();

        List<String> importantSkills = Arrays.asList(
                "javascript", "react", "node", "express", "mongodb",
                "typescript", "docker", "aws", "rest api", "git",
                "spring boot", "jwt", "sql", "cloud", "ci/cd");

        int matched = 0;

        for (String skill : importantSkills) {
            if (resume.contains(skill) && jd.contains(skill)) {
                matched++;
            }
        }

        int score = Math.min(95, 45 + matched * 7);

        return AnalyzeResponse.builder()
                .atsScore(score)
                .strengths(List.of(
                        "Resume contains relevant technical project experience.",
                        "Candidate demonstrates practical full-stack development exposure.",
                        "Good alignment with software engineering fundamentals."))
                .weaknesses(List.of(
                        "Some job-specific keywords may be missing.",
                        "Resume can improve by adding more quantified impact."))
                .missingSkills(List.of(
                        "Add missing tools or technologies from the job description.",
                        "Mention deployment, scalability, and production-level implementation if applicable."))
                .suggestions(List.of(
                        "Add measurable impact in project bullet points.",
                        "Include exact keywords from the target job description.",
                        "Highlight backend APIs, authentication, database design, and deployment."))
                .summary(
                        "The resume has a good technical foundation but can be improved with stronger keyword alignment and measurable project impact.")
                .build();
    }
}