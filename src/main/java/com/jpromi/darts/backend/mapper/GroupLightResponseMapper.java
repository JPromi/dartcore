package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.AccountGroupMember;
import com.jpromi.darts.backend.models.GroupLightResponse;
import com.jpromi.darts.backend.models.GroupResponse;
import com.jpromi.darts.backend.models.ProfileLightResponse;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupLightResponseMapper {

    @Autowired
    private UrlService urlService;

    @Autowired
    private ProfileLightResponseMapper profileLightResponseMapper;

    public GroupLightResponse fromAccountGroup(AccountGroup group, Account account) {
        Boolean isMember = false;
        if (account != null) {
            for (AccountGroupMember member : group.getMembers()) {
                if (member.getAccount().getId().equals(account.getId())) {
                    isMember = true;
                    break;
                }
            }
        }

        if (isMember || group.getIsPublic()) {
            return GroupLightResponse.builder()
                    .uuid(group.getUuid())
                    .name(group.getName())
                    .avatar(urlService.toPublicUrl(group.getAvatar(), "/static/files/placeholder/group.svg"))
                    .banner(urlService.toPublicUrl(group.getBanner()))
                    .isPublic(group.getIsPublic())
                    .isMember(isMember)
                    .membersTotal(group.getMembers() != null ? group.getMembers().size() : 0L)
                    .build();
        } else {
            return GroupLightResponse.builder()
                    .uuid(group.getUuid())
                    .name(group.getName())
                    .avatar(urlService.toPublicUrl(group.getAvatar(), "/static/files/placeholder/group.svg"))
                    .banner(urlService.toPublicUrl(group.getBanner()))
                    .isPublic(group.getIsPublic())
                    .isMember(isMember)
                    .membersTotal(0L)
                    .build();
        }
    }

}
