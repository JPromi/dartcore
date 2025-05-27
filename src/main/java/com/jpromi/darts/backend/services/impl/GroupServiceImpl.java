package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.*;
import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import com.jpromi.darts.backend.mapper.*;
import com.jpromi.darts.backend.models.*;
import com.jpromi.darts.backend.repositories.AccountGroupInvitationAccountRepository;
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

    @Autowired
    private AccountGroupInvitationAccountRepository accountGroupInvitationAccountRepository;

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

        // Member
        group.setMembers(List.of(AccountGroupMember.builder().account(account).accountGroup(group).isOwner(true).build()));
        // remove duplicates
        ArrayList<AccountGroupInvitationAccount> invitaion = new ArrayList<>();
        if(groupRequest.getMembers() != null) {
            for (String member : groupRequest.getMembers()) {
                Account invitaionAccount = accountRepository.findByUuidAndIsDisabledFalseAndIsDeletedFalseAndIsEmailVerifiedTrue(UUID.fromString(member));
                if (invitaionAccount != null) {
                    AccountGroupInvitationAccount groupInvitation = AccountGroupInvitationAccount.builder()
                            .account(invitaionAccount)
                            .inviter(account)
                            .accountGroup(group)
                            .build();
                    invitaion.add(groupInvitation);
                }
            }
        }
        // remove duplicates
        invitaion = new ArrayList<>(new HashSet<>(invitaion));

        group.setInvitations(invitaion);

        accountGroupRepository.save(group);

        return groupResponseMapper.fromAccountGroup(group, account);
    }

    @Override
    public Void deleteGroup(UUID uuid, Account account) {
        AccountGroup group = accountGroupRepository.findByUuid(uuid);
        // check if is owner
        if (group != null) {
            AccountGroupMember groupMember = group.getMembers().stream()
                    .filter(member -> member.getAccount().getId().equals(account.getId()))
                    .findFirst()
                    .orElse(null);
            if (groupMember != null && groupMember.getIsOwner()) {
                accountGroupRepository.delete(group);
            } else {
                throw new RuntimeException("You are not the owner of this group");
            }
        }
        return null;
    }

    @Override
    public List<GroupInvitationResponse> getAccountInvitations(Account account, InvitationStatusAccountEnum status) {
        List<AccountGroupInvitationAccount> invitations = accountGroupInvitationAccountRepository.findByAccountAndStatus(account, status);
        return invitations.stream()
                .map(invitation -> GroupInvitationResponse.builder()
                        .uuid(invitation.getUuid())
                        .group(groupLightResponseMapper.fromAccountGroup(invitation.getAccountGroup(), account))
                        .status(invitation.getStatus())
                        .inviter(profileLightResponseMapper.fromAccount(invitation.getInviter()))
                        .createdAt(invitation.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public Long countAccountInvitations(Account account, InvitationStatusAccountEnum status) {
        return accountGroupInvitationAccountRepository.countByAccountAndStatus(account, status);
    }

    @Override
    public Void responseInvitation(UUID uuid, Account account, InvitationStatusAccountEnum status) {
        AccountGroupInvitationAccount invitation = accountGroupInvitationAccountRepository.findByUuidAndAccount(uuid, account);
        if (invitation != null) {
            if(invitation.getStatus() == InvitationStatusAccountEnum.PENDING) {
                AccountGroup group = invitation.getAccountGroup();
                if (status == InvitationStatusAccountEnum.ACCEPTED) {
                    AccountGroupMember groupMember = AccountGroupMember.builder()
                            .account(account)
                            .accountGroup(group)
                            .isOwner(false)
                            .isAdmin(invitation.getIsAdmin())
                            .build();
                    group.getMembers().add(groupMember);
                    accountGroupRepository.save(group);
                }

                invitation.setStatus(status);
                accountGroupInvitationAccountRepository.save(invitation);
            } else {
                throw new RuntimeException("Invitation already answered");
            }
        } else {
            throw new RuntimeException("Invitation not found");
        }
        return null;
    }
}
