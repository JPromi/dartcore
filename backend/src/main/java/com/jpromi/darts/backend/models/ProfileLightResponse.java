package com.jpromi.darts.backend.models;

import com.jpromi.darts.backend.enums.ProfileVisibilityEnum;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ProfileLightResponse {

    private UUID uuid;
    private String username;
    private String avatar;
    private ProfileVisibilityEnum visibility;

}
