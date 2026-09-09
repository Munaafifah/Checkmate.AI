package com.checkmate.backend.service;

import com.checkmate.backend.exception.ResumeParsingException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeParsingService {

    private static final int MIN_MEANINGFUL_CHARACTERS = 20;

    public String extractTextFromPdf(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            String text = new PDFTextStripper().getText(document).trim();
            if (text.length() < MIN_MEANINGFUL_CHARACTERS) {
                throw new ResumeParsingException(
                        "Couldn't extract readable text from this PDF (it may be a scanned image). " +
                                "Please paste your resume text instead.");
            }
            return text;
        } catch (IOException e) {
            throw new ResumeParsingException(
                    "Failed to read the PDF file. Please paste your resume text instead.", e);
        }
    }
}
