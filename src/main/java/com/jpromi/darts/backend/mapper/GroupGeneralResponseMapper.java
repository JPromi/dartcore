package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.AccountGroupMember;
import com.jpromi.darts.backend.models.GroupGeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupGeneralResponseMapper {

    @Autowired
    private FileResponseMapper fileResponseMapper;

    public GroupGeneralResponse fromGroup(AccountGroup group, Account account) {
        Boolean isAdmin = false;
        Boolean isOwner = false;
        if (account != null) {
            for (AccountGroupMember member : group.getMembers()) {
                if (member.getAccount().getId().equals(account.getId())) {
                    if (member.getIsOwner()) {
                        isOwner = true;
                    } else if (member.getIsAdmin()) {
                        isAdmin = true;
                    }
                    break;
                }
            }
        }
        return GroupGeneralResponse.builder()
                .uuid(group.getUuid())
                .name(group.getName())
                .description(group.getDescription())
                .avatar(fileResponseMapper.fromFile(group.getAvatar()))
                .banner(fileResponseMapper.fromFile(group.getBanner()))
                .isPublic(group.getIsPublic())
                .isAdmin(isAdmin)
                .isOwner(isOwner)
                .build();
    }


    }
