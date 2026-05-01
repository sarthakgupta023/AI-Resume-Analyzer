package com.resumeiq.analyzer.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resumeiq.analyzer.dto.AnalyzeResponse;
import com.resumeiq.analyzer.model.Analysis;
import com.resumeiq.analyzer.service.ResumeAnalysisService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeAnalysisService resumeAnalysisService;

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ResumeIQ backend is running");
    }

    @PostMapping("/analyze")
    public AnalyzeResponse analyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription,
            Principal principal) {
        return resumeAnalysisService.analyze(file, jobDescription, principal.getName());
    }

    @GetMapping("/history")
    public List<Analysis> getHistory(Principal principal) {
        return resumeAnalysisService.getHistory(principal.getName());
    }
}