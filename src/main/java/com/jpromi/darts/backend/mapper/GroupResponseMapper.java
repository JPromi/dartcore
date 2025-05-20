package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.AccountGroupMember;
import com.jpromi.darts.backend.models.GroupResponse;
import com.jpromi.darts.backend.models.ProfileLightResponse;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupResponseMapper {

    @Autowired
    private UrlService urlService;

    @Autowired
    private ProfileLightResponseMapper profileLightResponseMapper;

    public GroupResponse fromAccountGroup(AccountGroup group, Account account) {
        Boolean isMember = false;
        if (account != null) {
            for (AccountGroupMember member : group.getMembers()) {
                if (member.getAccount().getId().equals(account.getId())) {
                    isMember = true;
                    break;
                }
            }
        }

        List<ProfileLightResponse> members = null;

        if (group.getMembers() != null && !group.getMembers().isEmpty()) {
            members = group.getMembers().stream()
                    .map(member -> profileLightResponseMapper.fromAccount(member.getAccount()))
                    .toList();
        }

        return GroupResponse.builder()
                .uuid(group.getUuid())
                .name(group.getName())
                .description(group.getDescription())
                .avatar(urlService.toPublicUrl(group.getAvatar()))
                .banner(urlService.toPublicUrl(group.getBanner()))
                .isPublic(group.getIsPublic())
                .isMember(isMember)
                .members(members)
                .createdAt(group.getCreatedAt())
                .build();
    }

}
