package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.services.TemplateService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Override
    public String generateTemplate(String htmlContent, HashMap<String, String> variables) {
        htmlContent = htmlContent.replaceAll("\\{\\{\\s*(\\w+)\\s*\\}\\}", "{{$1}}");
        htmlContent = htmlContent.replaceAll("<raw>[\\s\\S]*?</raw>", "");
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

    @Override
    public String generatePlainText(String htmlContent, HashMap<String, String> variables) {
        htmlContent = htmlContent.replaceAll("\\{\\{\\s*(\\w+)\\s*\\}\\}", "{{$1}}");
        for (String key : variables.keySet()) {
            htmlContent = htmlContent.replace("{{" + key + "}}", variables.get(key));
        }

        Pattern pattern = Pattern.compile(
                "<raw\\b[^>]*>(.*?)</raw>",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );

        Matcher m = pattern.matcher(htmlContent);
        StringBuilder result = new StringBuilder();

        while (m.find()) {
            String text = m.group(1).strip();
            if (!text.isEmpty()) {
                if (result.length() > 0) {
                    result.append(System.lineSeparator());
                }
                result.append(text);
            }
        }

        return result.toString();
    }

    @Override
    public String generatePlainTextFromFile(String filePath, HashMap<String, String> variables) {
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
        return generatePlainText(content, variables);
    }
}
