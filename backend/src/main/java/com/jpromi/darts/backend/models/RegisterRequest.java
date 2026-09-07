package com.jpromi.darts.backend.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
}
