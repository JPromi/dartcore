package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LocationRequest {
    private String name;
    private String description;
    private Boolean isPublic;
    private String address;
    private List<Screen> screens;
    private List<Client> clients;

    @Data
    @Builder
    public static class Client {
        private UUID uuid;
        private String name;
    }

    @Data
    @Builder
    public static class Screen {
        private UUID uuid;
        private String name;
    }
}
