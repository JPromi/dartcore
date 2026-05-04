package com.jpromi.darts.backend.models;

import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import com.jpromi.darts.backend.enums.ProfileVisibilityEnum;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GroupMemberAdminResponse {
    private UUID uuid;
    private String username;
    private String avatar;
    private ProfileVisibilityEnum visibility;
    private Boolean isAdmin;
    private Boolean isOwner;
    private InvitationStatusAccountEnum status;
}
