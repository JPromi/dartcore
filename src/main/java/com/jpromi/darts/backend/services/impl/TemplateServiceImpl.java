package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.services.TemplateService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Override
    public String generateTemplate(String htmlContent, HashMap<String, String> variables) {
        htmlContent = htmlContent.replaceAll("\\{\\{\\s*(\\w+)\\s*\\}\\}", "{{$1}}");
        for (String key : variables.keySet()) {
            htmlContent = htmlContent.replace("{{" + key + "}}", variables.get(key));
        }
        return htmlContent;
    }

    @Override
    public String generateTemplateFromFile(String filePath, HashMap<String, String> variables) {
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = contentBuilder.toString();

        return generateTemplate(content, variables);
    }
}
