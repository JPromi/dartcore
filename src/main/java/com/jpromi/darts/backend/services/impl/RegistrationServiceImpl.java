package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.Profile;
import com.jpromi.darts.backend.models.RegisterRequest;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.password4j.Password;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private AccountRepository accountRepository;

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
                .registrationTimestamp(OffsetDateTime.now())
                .profile(profile)
                .build();

        accountRepository.save(account);

        return account;
    }

    @Override
    public Boolean validate(String token) {
        Optional<Account> accountCheck = accountRepository.findByEmailVerificationTokenAndIsEmailVerifiedFalseAndIsDeletedFalseAndIsDisabledFalse(token);
        if (accountCheck.isPresent()) {
            Account account = accountCheck.get();
            account.setEmailVerificationToken(null);
            account.setIsEmailVerified(true);
            account.setEmailVerificationTimestamp(OffsetDateTime.now());

            accountRepository.save(account);
            return true;
        } else {
            return false;
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
