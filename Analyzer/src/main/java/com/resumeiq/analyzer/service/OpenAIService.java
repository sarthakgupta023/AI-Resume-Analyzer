package com.resumeiq.analyzer.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAIService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public AnalyzeResponse analyzeResume(String resumeText, String jobDescription) {

        try {
            String prompt = buildPrompt(resumeText, jobDescription);

            String aiText = chatClient.prompt()
                    .system("You are an expert ATS resume evaluator and technical recruiter. Return only valid JSON.")
                    .user(prompt)
                    .call()
                    .content();

            return parseAiResponse(aiText);

        } catch (Exception e) {
            e.printStackTrace();
            return fallbackAnalysis(resumeText, jobDescription);
        }
    }

    private String buildPrompt(String resumeText, String jobDescription) {
        return """
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

                Scoring rules:
                - 90-100: excellent match
                - 75-89: strong match
                - 60-74: decent match
                - below 60: weak match

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
            throw new RuntimeException("Failed to parse OpenAI response");
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
        if (text == null) {
            return "";
        }

        return text.length() > 12000 ? text.substring(0, 12000) : text;
    }

    private AnalyzeResponse fallbackAnalysis(String resumeText, String jobDescription) {

        String resume = resumeText == null ? "" : resumeText.toLowerCase();
        String jd = jobDescription == null ? "" : jobDescription.toLowerCase();

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