package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.Profile;
import com.jpromi.darts.backend.models.EmailObject;
import com.jpromi.darts.backend.models.RegisterRequest;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.services.MailService;
import com.jpromi.darts.backend.services.RegistrationService;
import com.jpromi.darts.backend.services.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import com.password4j.Password;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private TemplateService templateService;

    @Value("${com.jpromi.darts.app.domain}")
    private String appDomain;

    @Override
    public Account register(RegisterRequest registerRequest) {
        String password = Password.hash(registerRequest.getPassword()).withArgon2().getResult();
        Profile profile = Profile.builder().build();
        Account account = Account.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(password)
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .isEmailVerified(false)
                .emailVerificationToken(generateValidationToken())
                .registrationTimestamp(Instant.now())
                .profile(profile)
                .build();



        try {
            accountRepository.save(account);
        } catch (DataIntegrityViolationException ex) {
            // Unique-Constraint / FK / etc.
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User with the same username already exists",
                    ex
            );
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while validating the account",
                    ex
            );
        }

        EmailObject mailObject = EmailObject.builder()
                .to(new ArrayList<>(List.of(registerRequest.getEmail())))
                .subject("Email Verification")
                .build();

        HashMap<String, String> templateVariables = new HashMap<>(
            Map.of(
            "confirmationLink", appDomain + "/barrier/register/" + account.getEmailVerificationToken(),
            "name", registerRequest.getUsername()
            )
        );

        mailObject.setHtmlBody(
            templateService.generateTemplateFromFile(
                "templates/mail/registration.html",
                templateVariables
            )
        );

        mailObject.setBody(
            templateService.generatePlainTextFromFile(
                "templates/mail/registration.html",
                templateVariables
            )
        );

        mailService.send(mailObject);

        return account;
    }

    @Override
    public String validate(String token) {
        Optional<Account> accountCheck = accountRepository.findByEmailVerificationTokenAndIsEmailVerifiedFalseAndIsDeletedFalseAndIsDisabledFalse(token);
        if (accountCheck.isPresent()) {
            Account account = accountCheck.get();
            account.setEmailVerificationToken(null);
            account.setIsEmailVerified(true);
            account.setEmailVerificationTimestamp(Instant.now());

            accountRepository.save(account);
            return account.getUsername();
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Invalid verification token");
        }
    }

    private String generateValidationToken() {
        return generateValidationToken(64L);
    }

    private String generateValidationToken(Long length) {
        StringBuilder token = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            token.append(characters.charAt(index));
        }
        return token.toString();
    }
}
