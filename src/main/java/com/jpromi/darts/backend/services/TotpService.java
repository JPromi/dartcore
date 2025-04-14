package com.jpromi.darts.backend.services;

public interface TotpService {
    public String generateSecret();
    public String generateTotp(String secret);
    public boolean validateTotp(String secret, String totp);
    public String[] generateRecoveryCodes();
    public String[] generateRecoveryCodes(Integer number);
}
