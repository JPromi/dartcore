package com.jpromi.darts.backend.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime dateOfBirth;
}
