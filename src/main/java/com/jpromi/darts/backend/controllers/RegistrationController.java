package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.RegisterRequest;
import com.jpromi.darts.backend.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("RegistrationController")
@RequestMapping("/api/register")
public class RegistrationController {
    /*
    Features:
    POST - register
    UPDATE - validate
    GET - validate
    */

    @Autowired
    RegistrationService registrationService;

    @PostMapping("")
    public ResponseEntity<Account> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(registrationService.register(request));
    }

    @PutMapping("/validate")
    public ResponseEntity<String> validate(@RequestBody String token) {
        String username = registrationService.validate(token);
        return ResponseEntity.ok("\"" + username + "\"");
    }
}
