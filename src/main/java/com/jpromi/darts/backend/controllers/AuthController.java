package com.jpromi.darts.backend.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("AuthController")
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login");
    }
}
