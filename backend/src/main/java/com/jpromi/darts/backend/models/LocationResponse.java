package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LocationResponse {
    private UUID uuid;
    private String name;
    private String description;
    private String address;
    private Boolean isPublic;
    private List<String> screens;
    private List<String> inputClients;
}
