package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GroupLightResponse {

    private UUID uuid;
    private String name;
    private String avatar;
    private String banner;
    private Long membersTotal;
    private Boolean isMember;
    private Boolean isPublic;

}
