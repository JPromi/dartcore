package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.enums.ErrorCode;
import com.jpromi.darts.backend.models.LoginRequest;
import com.jpromi.darts.backend.models.LoginResponse;
import com.jpromi.darts.backend.models.SessionAccountResponse;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.repositories.SessionRepository;
import com.jpromi.darts.backend.services.AuthService;
import com.password4j.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TotpServiceImpl totpService;

    @Autowired
    private UrlServiceImpl urlService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse loginResponse = LoginResponse.builder()
                .token("")
                .totpRequired(false)
                .build();

        Optional<Account> accountCheck = accountRepository.findByUsernameAndIsDisabledFalseAndIsDeletedFalseAndIsEmailVerifiedTrue(loginRequest.getUsername());

        if (accountCheck.isPresent()) {
            Account account = accountCheck.get();
            boolean passwordValidated = Password.check(loginRequest.getPassword(), account.getPassword()).withArgon2();
            if (passwordValidated) {
                Session session = this.createSession(account);
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
    public LoginResponse totp(String sessionToken, String totp) {
        LoginResponse loginResponse = LoginResponse.builder()
                .token("")
                .totpRequired(false)
                .build();

        Session session = this.sessionRepository.findByTokenAndIsActiveTrueAndNeedsTotpTrue(sessionToken);

        if(session != null) {
            Account account = this.accountRepository.findById(session.getAccountId()).orElse(null);
            if(account != null) {
                if(account.getIsTotpEnabled()) {
                    if(totpService.validateTotp(account.getTotpSecret(), totp)) {
                        session.setNeedsTotp(false);
                        this.sessionRepository.save(session);
                        loginResponse.setToken(session.getToken());
                    } else {
                        loginResponse.setError(ErrorCode.INVALID_TOTP);
                    }
                } else {
                    loginResponse.setError(ErrorCode.TOTP_DISABLED);
                }
            } else {
                loginResponse.setError(ErrorCode.INVALID_LOGIN);
            }
        } else {
            loginResponse.setError(ErrorCode.INVALID_SESSION);
        }

        return loginResponse;
    }

    @Override
    public Boolean logout(String token) {
        Session session = this.sessionRepository.findByTokenAndIsActiveTrue(token);

        if(session != null) {
            session.setIsActive(false);
            this.sessionRepository.save(session);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Session session(String token) {
        return this.sessionRepository.findByTokenAndIsActiveTrueAndNeedsTotpFalse(token);
    }

    @Override
    public Session generalSession(String token) {
        return this.sessionRepository.findByTokenAndIsActiveTrue(token);
    }

    @Override
    public SessionAccountResponse accountBySession(String token) {
        Session session = this.session(token);
        if (session != null) {
            Account account = this.accountRepository.findById(session.getAccountId()).orElse(null);
            if (account != null) {
                return SessionAccountResponse.builder()
                        .uuid(account.getUuid())
                        .firstName(account.getFirstName())
                        .lastName(account.getLastName())
                        .email(account.getEmail())
                        .username(account.getUsername())
                        .avatar(account.getAvatar() != null ? urlService.toPublicUrl(account.getAvatar().getRealPath()) : null)
                        .registrationTimestamp(account.getEmailVerificationTimestamp())
                        .build();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Boolean logoutBySession(String token) {
        Session session = this.session(token);
        if (session != null) {
            session.setIsActive(false);
            this.sessionRepository.save(session);
            return true;
        } else {
            return false;
        }
    }

    private Session createSession(Account account) {
        return Session.builder()
                .accountId(account.getId())
                .token(this.generateToken())
                .needsTotp(account.getIsTotpEnabled())
                .isActive(true)
                .build();
    }

    private String generateToken() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456780!$";
        StringBuilder token = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 64; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }
}
