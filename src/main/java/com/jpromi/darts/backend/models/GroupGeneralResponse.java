package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GroupGeneralResponse {
    private UUID uuid;
    private String name;
    private String description;
    private FileResponse avatar;
    private FileResponse banner;
    private Boolean isPublic;
    private Boolean isAdmin;
    private Boolean isOwner;
}
