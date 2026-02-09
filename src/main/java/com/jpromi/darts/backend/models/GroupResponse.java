package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GroupResponse {

    private UUID uuid;
    private String name;
    private String description;
    private String avatar;
    private String banner;
    private Boolean isMember;
    private Boolean isPublic;
    private Boolean isAdmin;
    private Boolean isOwner;
    private List<ProfileLightResponse> members;
    private OffsetDateTime createdAt;

}
