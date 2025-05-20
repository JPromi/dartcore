package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.AccountGroupMember;
import com.jpromi.darts.backend.entities.File;
import com.jpromi.darts.backend.mapper.*;
import com.jpromi.darts.backend.models.*;
import com.jpromi.darts.backend.repositories.AccountGroupRepository;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.services.FileService;
import com.jpromi.darts.backend.services.GroupService;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private AccountGroupRepository accountGroupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UrlService urlService;

    @Autowired
    private ProfileLightResponseMapper profileLightResponseMapper;

    @Autowired
    private GroupResponseMapper groupResponseMapper;

    @Autowired
    private GroupLightResponseMapper groupLightResponseMapper;

    @Autowired
    private PageResponseMapper pageResponseMapper;

    @Autowired
    private FileService fileService;

    @Override
    public List<GroupLightResponse> getGroupsByAccount(Long accountId) {

        Account account = accountRepository.findById(accountId).orElse(null);

        if (account != null) {
            return getGroupsByAccount(account);
        } else {
            return null;
        }
    }

    @Override
    public List<GroupLightResponse> getGroupsByAccount(Account account) {
        List<GroupLightResponse> groups = new ArrayList<>();

        List<AccountGroupMember> accountGroups = account.getGroupMemberships();

        for (AccountGroupMember groupMember : accountGroups) {
            groups.add(groupLightResponseMapper.fromAccountGroup(groupMember.getAccountGroup(), account));
        }

        return groups;
    }

    @Override
    public GroupResponse getGroupByUuid(UUID uuid, Account account) {
        AccountGroup group = accountGroupRepository.findByUuid(uuid);
        if (group != null) {
            if (account != null) {
                return groupResponseMapper.fromAccountGroup(group, account);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public PageResponse<GroupLightResponse> searchGroups(String query, Account account, Pageable pageable, Boolean isMember, Boolean isPublic) {
        Page<AccountGroup> groups = accountGroupRepository.searchGroups(query, account.getId(), isMember, isPublic, pageable);

        return pageResponseMapper.fromPage(groups.map(group -> groupLightResponseMapper.fromAccountGroup(group, account)));
    }

    @Override
    public GroupResponse createGroup(GroupRequest groupRequest, Account account) {
        AccountGroup group = AccountGroup.builder()
                .name(groupRequest.getName())
                .description(groupRequest.getDescription())
                .isPublic(groupRequest.getIsPublic())
                .build();

        // Avatar
        if(groupRequest.getAvatar() != null) {
            File banner = fileService.getFileByUuid(group.getAvatar().getUuid());
            if(banner != null) {
                banner.setIsTmporary(false);
                group.setAvatar(banner);
            }
        }

        // Banner
        if(groupRequest.getBanner() != null) {
            File banner = fileService.getFileByUuid(group.getBanner().getUuid());
            if(banner != null) {
                banner.setIsTmporary(false);
                group.setBanner(banner);
            }
        }

        // Members
        // remove duplicates
        ArrayList<AccountGroupMember> members = new ArrayList<>();
        if(groupRequest.getMembers() != null) {
            for (String member : groupRequest.getMembers()) {
                Account memberAccount = accountRepository.findByUuidAndIsDisabledFalseAndIsDeletedFalseAndIsEmailVerifiedTrue(UUID.fromString(member));
                if (memberAccount != null) {
                    AccountGroupMember groupMember = AccountGroupMember.builder()
                            .account(memberAccount)
                            .accountGroup(group)
                            .build();
                    members.add(groupMember);
                }
            }
        }
        members.add(AccountGroupMember.builder().account(account).accountGroup(group).isOwner(true).build());
        // remove duplicates
        members = new ArrayList<>(new HashSet<>(members));

        group.setMembers(members);

        accountGroupRepository.save(group);

        return groupResponseMapper.fromAccountGroup(group, account);
    }
}
