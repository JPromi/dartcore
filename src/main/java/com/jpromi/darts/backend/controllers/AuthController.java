package com.jpromi.darts.backend.controllers;


import com.jpromi.darts.backend.models.LoginRequest;
import com.jpromi.darts.backend.models.LoginResponse;
import com.jpromi.darts.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

//    @Autowired
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }

    @PostMapping("")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(this.authService.login(request));
//        return ResponseEntity.ok("Login");
    }
}
