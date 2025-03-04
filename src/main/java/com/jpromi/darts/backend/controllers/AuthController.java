package com.jpromi.darts.backend.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login");
    }
}
