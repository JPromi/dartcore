package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.models.LoginRequest;
import com.jpromi.darts.backend.models.LoginResponse;
import com.jpromi.darts.backend.models.SessionAccountResponse;
import org.springframework.stereotype.Service;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse totp(String session, String totp);
    Boolean logout(String token);
    Session session(String token);
    Session generalSession(String token);
    SessionAccountResponse accountBySession(String token);
    Boolean logoutBySession(String token);
}
