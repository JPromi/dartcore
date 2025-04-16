package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GroupResponse {

    private UUID uuid;
    private String name;
    private String description;
    private String avatar;
    private Boolean isPublic;
    private List<ProfileResponse> members;

}
