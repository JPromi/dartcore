package com.jpromi.darts.backend.services;

import java.util.ArrayList;
import java.util.HashMap;

public interface TemplateService {
    String generateTemplate(String htmlContent, HashMap<String, String> variables);
    String generateTemplateFromFile(String filePath, HashMap<String, String> variables);
    String generatePlainText(String htmlContent, HashMap<String, String> variables);
    String generatePlainTextFromFile(String filePath, HashMap<String, String> variables);
}
