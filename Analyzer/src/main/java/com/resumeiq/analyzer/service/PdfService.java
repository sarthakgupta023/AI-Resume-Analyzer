package com.resumeiq.analyzer.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfService {

    public String extractText(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("PDF file is required");
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        try {
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();

            String text = stripper.getText(document);
            document.close();

            if (text == null || text.trim().isEmpty()) {
                throw new RuntimeException("Could not extract text from PDF");
            }

            return text;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read PDF file");
        }
    }
}