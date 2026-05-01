package com.resumeiq.analyzer.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.resumeiq.analyzer.model.Analysis;

public interface AnalysisRepository extends MongoRepository<Analysis, String> {

    List<Analysis> findByUserEmailOrderByCreatedAtDesc(String userEmail);
}