package com.resumeiq.analyzer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.resumeiq.analyzer.dto.AnalyzeResponse;
import com.resumeiq.analyzer.model.Analysis;
import com.resumeiq.analyzer.repository.AnalysisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeAnalysisService {

    private final PdfService pdfService;
    private final OpenAIService openAIService;
    private final AnalysisRepository analysisRepository;

    public AnalyzeResponse analyze(
            MultipartFile file,
            String jobDescription,
            String userEmail) {

        if (jobDescription == null || jobDescription.trim().isEmpty()) {
            throw new RuntimeException("Job description is required");
        }

        String resumeText = pdfService.extractText(file);

        AnalyzeResponse response = openAIService.analyzeResume(resumeText, jobDescription);

        Analysis analysis = Analysis.builder()
                .userEmail(userEmail)
                .fileName(file.getOriginalFilename())
                .jobDescription(jobDescription)
                .atsScore(response.getAtsScore())
                .strengths(response.getStrengths())
                .weaknesses(response.getWeaknesses())
                .missingSkills(response.getMissingSkills())
                .suggestions(response.getSuggestions())
                .summary(response.getSummary())
                .createdAt(LocalDateTime.now())
                .build();

        analysisRepository.save(analysis);

        return response;
    }

    public List<Analysis> getHistory(String userEmail) {
        return analysisRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }
}