package com.resumeiq.analyzer.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analyses")
public class Analysis {

    @Id
    private String id;

    private String userEmail;
    private String fileName;
    private String jobDescription;

    private int atsScore;

    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> missingSkills;
    private List<String> suggestions;

    private String summary;

    private LocalDateTime createdAt;
}