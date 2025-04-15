package com.jpromi.darts.backend.controllers;


import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.enums.ErrorCode;
import com.jpromi.darts.backend.models.LoginRequest;
import com.jpromi.darts.backend.models.LoginResponse;
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
    public ResponseEntity<Session> session(@CookieValue("b2h.darts.session") String sessionCookie) {
        if(sessionCookie != null) {
            Session response = this.authService.session(sessionCookie);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
