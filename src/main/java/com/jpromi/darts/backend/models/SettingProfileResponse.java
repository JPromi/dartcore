package com.jpromi.darts.backend.models;

import com.jpromi.darts.backend.enums.ProfileVisibilityEnum;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SettingProfileResponse {
    private UUID uuid;
    private String username;
    private String description;
    private FileResponse avatar;
    private FileResponse banner;
    private String country;
    private ProfileVisibilityEnum visibility;
    private ProfileResponse.Links links;
}
