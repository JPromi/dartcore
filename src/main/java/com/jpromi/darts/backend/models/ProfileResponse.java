package com.jpromi.darts.backend.models;

import com.jpromi.darts.backend.enums.ProfileVisibilityEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ProfileResponse {

    private UUID uuid;
    private String username;
    private String description;
    private String country;
    private Integer age;
    private String avatar;
    private String banner;
    private Links links;
    private ProfileVisibilityEnum visibility;
    private OffsetDateTime createdAt;

    @Data
    public static class Links {
        private String web;
        private String x;
        private String instagram;
        private String facebook;
        private String youtube;
        private String github;
        private String twitch;
    }

}
