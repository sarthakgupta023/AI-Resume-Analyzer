package com.resumeiq.analyzer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeAnalysisService {

    private final PdfService pdfService;
    private final OpenAIService openAIService;
    private final AnalysisRepository analysisRepository;

    public ResumeAnalysisService(PdfService pdfService, OpenAIService openAIService,
            AnalysisRepository analysisRepository) {
        this.pdfService = pdfService;
        this.openAIService = openAIService;
        this.analysisRepository = analysisRepository;
    }

    public AnalyzeResponse analyze(MultipartFile file, String jobDescription, String userEmail) {
        if (jobDescription == null || jobDescription.trim().isEmpty()) {
            throw new RuntimeException("Job description is required");
        }

        String resumeText = pdfService.extractText(file);
        AnalyzeResponse response = openAIService.analyzeResume(resumeText, jobDescription);

        Analysis analysis = new Analysis();
        analysis.setUserEmail(userEmail);
        analysis.setFileName(file.getOriginalFilename());
        analysis.setJobDescription(jobDescription);
        analysis.setAtsScore(response.getAtsScore());
        analysis.setStrengths(response.getStrengths());
        analysis.setWeaknesses(response.getWeaknesses());
        analysis.setMissingSkills(response.getMissingSkills());
        analysis.setSuggestions(response.getSuggestions());
        analysis.setSummary(response.getSummary());
        analysis.setCreatedAt(LocalDateTime.now());

        analysisRepository.save(analysis);
        return response;
    }

    public List<Analysis> getHistory(String userEmail) {
        return analysisRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }
}