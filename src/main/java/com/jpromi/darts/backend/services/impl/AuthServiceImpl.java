package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.enums.ErrorCode;
import com.jpromi.darts.backend.models.LoginRequest;
import com.jpromi.darts.backend.models.LoginResponse;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.repositories.SessionRepository;
import com.jpromi.darts.backend.services.AuthService;
import com.password4j.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse loginResponse = LoginResponse.builder()
                .token("")
                .totpRequired(false)
                .build();

        Account user = accountRepository.findByUsername(loginRequest.getUsername());

        if (user != null) {
            boolean passwordValidated = Password.check(loginRequest.getPassword(), user.getPassword()).withArgon2();
            if (passwordValidated) {
                Session session = this.createSession(user);
                sessionRepository.save(session);

                loginResponse.setToken(session.getToken());
                loginResponse.setTotpRequired(session.getNeedsTotp());
            } else {
                loginResponse.setError(ErrorCode.INVALID_LOGIN);

            }
            return loginResponse;
        } else {
            loginResponse.setError(ErrorCode.INVALID_LOGIN);
            return loginResponse;
        }
    }

    @Override
    public Boolean logout(String token) {
        return null;
    }

    @Override
    public Session session(String token) {
        return null;
    }

    private Session createSession(Account account) {
        String token = "";
        token = Password.hash(token).withArgon2().toString();
        Session session = Session.builder()
                .accountId(account.getId())
                .token(null)
                .needsTotp(account.getIsTotpEnabled())
                .isActive(true)
                .build();

        if(!account.getIsTotpEnabled()) {
            session.setToken(this.generateToken());
        }
        return session;
    }

    private String generateToken() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456780!@#$%^&*()_+";
        StringBuilder token = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 64; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }
}
