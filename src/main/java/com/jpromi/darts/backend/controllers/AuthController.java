package com.jpromi.darts.backend.controllers;


import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.enums.ErrorCode;
import com.jpromi.darts.backend.models.LoginRequest;
import com.jpromi.darts.backend.models.LoginResponse;
import com.jpromi.darts.backend.models.SessionAccountResponse;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController("AuthController")
@RequestMapping("/api/auth")
public class AuthController {

    /*
    Features:
    POST - login
    DELETE, GET - logout
    POST - register
    POST - forgot password
    POST - reset password
    GET - session
    GET - user
    */

    @Autowired
    private AuthService authService;

    @PostMapping("")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = this.authService.login(request);

        return switch (response.getError()) {
            case NONE -> ResponseEntity.ok(response);
            case INVALID_LOGIN, INVALID_TOTP -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            default -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        };
    }

    @GetMapping("")
    public ResponseEntity<LoginResponse> loginSession(@CookieValue("b2h.darts.session") String sessionCookie) {
        if(sessionCookie != null) {
            Session session = this.authService.generalSession(sessionCookie);
            if (session != null) {
                LoginResponse response = LoginResponse.builder()
                        .token(session.getToken())
                        .totpRequired(session.getNeedsTotp())
                        .error(ErrorCode.NONE)
                        .build();
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<Void> logout(@CookieValue("b2h.darts.session") String sessionCookie) {
        if(sessionCookie != null) {
            this.authService.logout(sessionCookie);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/totp")
    public ResponseEntity<LoginResponse> totp(@CookieValue("b2h.darts.session") String sessionCookie, @RequestBody String request) {
        if(sessionCookie != null) {
            LoginResponse response = this.authService.totp(sessionCookie, request);

            return switch (response.getError()) {
                case NONE -> ResponseEntity.ok(response);
                case INVALID_LOGIN, INVALID_TOTP -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                default -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            };
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LoginResponse.builder().error(ErrorCode.INVALID_SESSION).build());
        }
    }

    @GetMapping("/session")
    public ResponseEntity<SessionAccountResponse> session(@CookieValue("b2h.darts.session") String sessionCookie) {
        if(sessionCookie != null) {
            SessionAccountResponse response = this.authService.accountBySession(sessionCookie);

            if (response != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
