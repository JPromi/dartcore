package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.models.LoginRequest;
import com.jpromi.darts.backend.models.LoginResponse;
import org.springframework.stereotype.Service;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    Boolean logout(String token);
    Session session(String token);
}
