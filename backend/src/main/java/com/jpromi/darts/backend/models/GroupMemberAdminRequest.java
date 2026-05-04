package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupMemberAdminRequest {
    private Boolean isAdmin = false;
}
