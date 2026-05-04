package com.jpromi.darts.backend.models;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
