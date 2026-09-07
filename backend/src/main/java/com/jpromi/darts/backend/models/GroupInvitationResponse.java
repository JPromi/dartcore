package com.jpromi.darts.backend.models;

import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GroupInvitationResponse {

    private UUID uuid;
    private GroupLightResponse group;
    private InvitationStatusAccountEnum status;
    private ProfileLightResponse inviter;
    private Instant createdAt;

}
