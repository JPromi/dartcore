package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.services.TotpService;
import org.springframework.stereotype.Service;
import dev.samstevens.totp.secret.*;
import dev.samstevens.totp.time.*;
import dev.samstevens.totp.code.*;

@Service
public class TotpServiceImpl implements TotpService {

    @Override
    public String generateSecret() {
        SecretGenerator secretGenerator = new DefaultSecretGenerator(128);
        return secretGenerator.generate();
    }

    @Override
    public String generateTotp(String secret) {
        return "";
    }

    @Override
    public boolean validateTotp(String secret, String totp) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return verifier.isValidCode(secret, totp);
    }

    @Override
    public String[] generateRecoveryCodes() {
        return generateRecoveryCodes(16);
    }

    @Override
    public String[] generateRecoveryCodes(Integer number)
    {
        String[] recoveryCodes = new String[number];
        for (int i = 0; i < number; i++) {
            recoveryCodes[i] = generateRecoveryCode();
        }
        return recoveryCodes;
    }

    private String generateRecoveryCode()
    {
        // format = "xxxx-xxxx
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            code.append(characters.charAt((int) (Math.random() * characters.length())));
        }
        code.append("-");
        for (int i = 0; i < 4; i++) {
            code.append(characters.charAt((int) (Math.random() * characters.length())));
        }

        return code.toString();
    }
}
