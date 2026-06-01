package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.Location;
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

    PageResponse<GroupLightResponse> searchGroups(String query, Account account, Pageable pageable, Boolean isMember, Boolean isPublic);

    GroupResponse createGroup(GroupRequest groupRequest, Account account);
    GroupGeneralResponse getGroupGeneralByUuid(UUID groupUuid, Account account);
    GroupGeneralResponse updateGroupGeneralByUuid(GroupGeneralRequest groupData, Account account);
    List<GroupMemberAdminResponse> getGroupMembersSettings(UUID groupUuid, Account account);
    Void updateGroupMemberSettings(UUID groupUuid, UUID memberUuid, GroupMemberAdminRequest request, Account account);
    Void removeMemberFromGroup(UUID groupUuid, UUID memberUuid, Account account);
    Void removeInvitationFromGroup(UUID groupUuid, UUID memberUuid, Account account);
    Void inviteAccountToGroup(UUID groupUuid, UUID accountUuid, Account account);
    Void deleteGroup(UUID uuid, Account account);

    Void leaveGroup(UUID groupUuid, Account account);

    List<GroupInvitationResponse> getAccountInvitations(Account account, InvitationStatusAccountEnum status);
    Void responseInvitation(UUID uuid, Account account, InvitationStatusAccountEnum status);
    Long countAccountInvitations(Account account, InvitationStatusAccountEnum status);

    LocationResponse getLocation(UUID groupUuid, Account account, UUID uuid);
    List<LocationResponse> getLocationsInGroup(UUID groupUuid, Account account);
    LocationResponse createLocation(UUID groupUuid, Account account, LocationRequest location);
    LocationResponse updateLocation(UUID groupUuid, Account account, UUID uuid, LocationRequest location);
    Void deleteLocation(UUID groupUuid, Account account, UUID uuid);
}
