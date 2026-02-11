package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroupInvitationAccount;
import com.jpromi.darts.backend.entities.AccountGroupMember;
import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import com.jpromi.darts.backend.models.GroupMemberAdminResponse;
import com.jpromi.darts.backend.models.ProfileLightResponse;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupMemberAdminResponseMapper {

    @Autowired
    private UrlService urlService;

    public GroupMemberAdminResponse fromAccountGroupMember(AccountGroupMember account) {
        return GroupMemberAdminResponse.builder()
                .uuid(account.getAccount().getUuid())
                .username(account.getAccount().getUsername())
                .avatar(urlService.toPublicUrl(account.getAccount().getAvatar()))
                .visibility(account.getAccount().getProfile().getVisibility())
                .isAdmin(account.getIsAdmin())
                .isOwner(account.getIsOwner())
                .status(InvitationStatusAccountEnum.ACCEPTED)
                .build();
    }

    public GroupMemberAdminResponse fromAccount(Account account, InvitationStatusAccountEnum status) {
        return GroupMemberAdminResponse.builder()
                .uuid(account.getUuid())
                .username(account.getUsername())
                .avatar(urlService.toPublicUrl(account.getAvatar()))
                .visibility(account.getProfile().getVisibility())
                .status(status)
                .isAdmin(false)
                .isOwner(false)
                .build();
    }

}
