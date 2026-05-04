package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SettingAccountRequest {
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordOld;
    private String passwordNew;
    private String passwordNewConfirm;
}
