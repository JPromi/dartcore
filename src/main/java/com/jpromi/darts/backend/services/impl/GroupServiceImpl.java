package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.AccountGroupMember;
import com.jpromi.darts.backend.mapper.ProfileResponseMapper;
import com.jpromi.darts.backend.models.GroupResponse;
import com.jpromi.darts.backend.models.ProfileResponse;
import com.jpromi.darts.backend.repositories.AccountGroupRepository;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.services.GroupService;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private AccountGroupRepository accountGroupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UrlService urlService;

    @Autowired
    private ProfileResponseMapper profileResponseMapper;

    @Override
    public List<GroupResponse> getGroupsByAccount(Long accountId) {

        Account account = accountRepository.findById(accountId).orElse(null);

        if (account != null) {
            return getGroupsByAccount(account);
        } else {
            return null;
        }
    }

    @Override
    public List<GroupResponse> getGroupsByAccount(Account account) {
        List<GroupResponse> groupsResponse = new ArrayList<>();

        List<AccountGroupMember> accountGroups = account.getGroupMemberships();

        for (AccountGroupMember groupMember : accountGroups) {
            AccountGroup group = groupMember.getAccountGroup();

            List<ProfileResponse> members = new ArrayList<>();
            for (AccountGroupMember member : group.getMembers()) {
                ProfileResponse profileResponse = profileResponseMapper.fromAccount(member.getAccount());
                members.add(profileResponse);
            }

            GroupResponse groupResponse = GroupResponse.builder()
                    .uuid(group.getUuid())
                    .name(group.getName())
                    .description(group.getDescription())
                    .avatar(group.getAvatar() != null ? urlService.toPublicUrl(group.getAvatar().getRealPath()) : null)
                    .isPublic(group.getIsPublic())
                    .members(members)
                    .build();
            groupsResponse.add(groupResponse);
        }

        return groupsResponse;
    }
}
