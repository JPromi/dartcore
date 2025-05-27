package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import com.jpromi.darts.backend.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    List<GroupLightResponse> getGroupsByAccount(Long accountId);
    List<GroupLightResponse> getGroupsByAccount(Account account);
    GroupResponse getGroupByUuid(UUID uuid, Account account);
    GroupResponse createGroup(GroupRequest groupRequest, Account account);
    Void deleteGroup(UUID uuid, Account account);
    PageResponse<GroupLightResponse> searchGroups(String query, Account account, Pageable pageable, Boolean isMember, Boolean isPublic);
    List<GroupInvitationResponse> getAccountInvitations(Account account, InvitationStatusAccountEnum status);
    Void responseInvitation(UUID uuid, Account account, InvitationStatusAccountEnum status);
    Long countAccountInvitations(Account account, InvitationStatusAccountEnum status);
}
