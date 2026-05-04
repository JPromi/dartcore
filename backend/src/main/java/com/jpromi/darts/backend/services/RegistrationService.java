package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.RegisterRequest;

public interface RegistrationService {
    public Account register(RegisterRequest register);
    public String validate(String token);
}
