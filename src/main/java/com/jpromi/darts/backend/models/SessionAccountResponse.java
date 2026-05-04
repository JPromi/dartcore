package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class SessionAccountResponse {
    private UUID uuid;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;
    private OffsetDateTime registrationTimestamp;
}
