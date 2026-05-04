package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.models.EmailObject;
import com.jpromi.darts.backend.services.MailService;
import com.jpromi.darts.backend.services.TemplateService;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {

    @Value("${mail.smtp.host}")
    private String mailHost;

    @Value("${mail.smtp.port}")
    private int mailPort;

    @Value("${mail.smtp.auth}")
    private boolean mailAuth;

    @Value("${mail.smtp.starttls.enable}")
    private boolean mailStarttls;

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${mail.password}")
    private String mailPassword;

    @Value("${com.jpromi.darts.web.domain}")
    private String webDomain;

    @Autowired
    private TemplateService templateService;

    @Override
    public void send(EmailObject mail) {
        HashMap<String, String> templateVariables = new HashMap<>(
                Map.of(
                        "urlWeb", webDomain,
                        "currentYear", String.valueOf(LocalDate.now().getYear()),
                        "content", mail.getHtmlBody(),
                        "title", mail.getSubject()
                )
        );
        // String htmlContent = templateService.generateTemplateFromFile("src/main/resources/templates/mail/container.html", templateVariables);

        try {
            Session session = getMailClient();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailUsername));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getTo().getFirst()));
            message.setSubject(mail.getSubject());
            // content
            MimeMultipart multipart = new MimeMultipart("alternative");

            MimeBodyPart messageTextPart = new MimeBodyPart();
            messageTextPart.setText(templateService.generatePlainText(mail.getHtmlBody(), templateVariables), "utf-8");

            MimeBodyPart messageHtmlPart = new MimeBodyPart();
            messageHtmlPart.setContent(templateService.generateTemplateFromFile("src/main/resources/templates/mail/container.html", templateVariables), "text/html; charset=utf-8");

            multipart.addBodyPart(messageTextPart);
            multipart.addBodyPart(messageHtmlPart);

            message.setContent(multipart);
            message.saveChanges();

            Transport.send(message);
            System.out.println("Email sent successfully to " + mail.getTo().getFirst());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private Session getMailClient() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", String.valueOf(mailAuth));
        props.put("mail.smtp.starttls.enable", String.valueOf(mailStarttls));
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.port", String.valueOf(mailPort));

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUsername, mailPassword);
            }
        });
    }
}
