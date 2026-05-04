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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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
    private GroupMemberAdminResponseMapper groupMemberAdminResponseMapper;

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

    @Autowired
    private GroupGeneralResponseMapper groupGeneralResponseMapper;

    @Value("${com.jpromi.darts.app.group.max-size}")
    private Integer maxGroupMembers;

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
    public GroupGeneralResponse getGroupGeneralByUuid(UUID groupUuid, Account account) {
        AccountGroup group = accountGroupRepository.findByUuid(groupUuid);

        if (group != null) {
            checkPermission(group, account, "admin");

            return groupGeneralResponseMapper.fromGroup(group, account);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public GroupGeneralResponse updateGroupGeneralByUuid(GroupGeneralRequest groupData, Account account) {
        AccountGroup group = accountGroupRepository.findByUuid(groupData.getUuid());

        if (group != null) {
            checkPermission(group, account, "admin");

            group.setName(groupData.getName());
            group.setDescription(groupData.getDescription());
            group.setIsPublic(groupData.getIsPublic());

            if(groupData.getAvatar() != null) {
                File avatar = fileService.getFileByUuid(groupData.getAvatar().getUuid());
                if(avatar != null) {
                    avatar.setIsTmporary(false);
                    group.setAvatar(avatar);
                }
            } else {
                if(group.getAvatar() != null) {
                    fileService.deleteFile(group.getAvatar().getUuid());
                }
                group.setAvatar(null);
            }

            if(groupData.getBanner() != null) {
                File banner = fileService.getFileByUuid(groupData.getBanner().getUuid());
                if(banner != null) {
                    banner.setIsTmporary(false);
                    group.setBanner(banner);
                }
            } else {
                if(group.getBanner() != null) {
                    fileService.deleteFile(group.getBanner().getUuid());
                }
                group.setBanner(null);
            }

            AccountGroup updatedGroup = accountGroupRepository.save(group);

            return groupGeneralResponseMapper.fromGroup(updatedGroup, account);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Void inviteAccountToGroup(UUID groupUuid, UUID accountUuid, Account account) {
        AccountGroup group = accountGroupRepository.findByUuid(groupUuid);
        Account invitationAccount = accountRepository.findByUuidAndIsDisabledFalseAndIsDeletedFalseAndIsEmailVerifiedTrue(accountUuid);

        // check if is owner or admin
        if (group != null && invitationAccount != null) {
            checkPermission(group, account, "admin");

            Long activeMembers = accountGroupRepository.countActiveMembersByGroupUuid(groupUuid);
            if (activeMembers >= maxGroupMembers) {
                throw new ResponseStatusException(HttpStatus.LOCKED, "Max group members reached (" + maxGroupMembers + ")");
            }

            if (isGroupMember(group, invitationAccount) || isGroupInvited(group, invitationAccount)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account already member or invited");
            } else {
                AccountGroupInvitationAccount groupInvitation = AccountGroupInvitationAccount.builder()
                        .account(invitationAccount)
                        .inviter(account)
                        .accountGroup(group)
                        .build();
                accountGroupInvitationAccountRepository.save(groupInvitation);
                return null;
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<GroupMemberAdminResponse> getGroupMembersSettings(UUID groupUuid, Account account) {
        AccountGroup group = accountGroupRepository.findByUuid(groupUuid);

        // check if is owner or admin
        if (group != null) {
            checkPermission(group, account, "admin");

            // accounts
            List<GroupMemberAdminResponse> members = new ArrayList<>();
            group.getMembers().forEach(member -> {
                members.add(groupMemberAdminResponseMapper.fromAccountGroupMember(member));
            });

            // invitations
            group.getInvitations().forEach(invitation -> {
                if (!invitation.getStatus().equals(InvitationStatusAccountEnum.ACCEPTED)) {
                    members.add(groupMemberAdminResponseMapper.fromAccount(invitation.getAccount(), invitation.getStatus()));
                }
            });

            // order by status and username
            members.sort(
                    Comparator.comparing(GroupMemberAdminResponse::getStatus,
                                    Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(GroupMemberAdminResponse::getUsername)
            );

            return members;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Void updateGroupMemberSettings(UUID groupUuid, UUID memberUuid, GroupMemberAdminRequest request, Account account) {
        AccountGroup group = accountGroupRepository.findByUuid(groupUuid);
        // check if is owner or admin
        if (group != null) {
            checkPermission(group, account, "admin");

            AccountGroupMember member = group.getMembers().stream()
                    .filter(m -> m.getAccount().getUuid().equals(memberUuid))
                    .findFirst()
                    .orElse(null);

            if (member != null) {
                if (!member.getIsOwner()) {
                    member.setIsAdmin(request.getIsAdmin());
                };

                accountGroupRepository.save(group);
                return null;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Void removeMemberFromGroup(UUID groupUuid, UUID memberUuid, Account account) {
        AccountGroup group = accountGroupRepository.findByUuid(groupUuid);
        // check if is owner or admin
        if (group != null) {
            checkPermission(group, account, "admin");

            AccountGroupMember member = group.getMembers().stream()
                    .filter(m -> m.getAccount().getUuid().equals(memberUuid))
                    .findFirst()
                    .orElse(null);

            if (member != null) {
                if (!member.getIsOwner()) {
                    int deletedEntries = accountGroupRepository.deleteMemberFromGroup(groupUuid, memberUuid);
                    if (deletedEntries == 0) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove member from group");
                    } else {
                        return null;
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove owner");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Void removeInvitationFromGroup(UUID groupUuid, UUID memberUuid, Account account) {
        AccountGroup group = accountGroupRepository.findByUuid(groupUuid);
        // check if is owner or admin
        if (group != null) {
            checkPermission(group, account, "admin");

            AccountGroupInvitationAccount invitation = group.getInvitations().stream()
                    .filter(i -> i.getAccount().getUuid().equals(memberUuid))
                    .findFirst()
                    .orElse(null);

            if (invitation != null) {
                int deletedEntries = accountGroupRepository.deleteInvitationFromGroup(groupUuid, memberUuid);
                if (deletedEntries == 0) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove invitation from group");
                } else {
                    return null;
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Void deleteGroup(UUID uuid, Account account) {
        AccountGroup group = accountGroupRepository.findByUuid(uuid);
        // check if is owner
        if (group != null) {
            checkPermission(group, account, "admin");

            accountGroupRepository.delete(group);
        }
        return null;
    }

    @Override
    public Void leaveGroup(UUID groupUuid, Account account) {
        AccountGroup group = accountGroupRepository.findByUuid(groupUuid);
        // check if is member
        if (group != null) {
            if (isGroupMember(group, account)) {
                AccountGroupMember member = group.getMembers().stream()
                        .filter(m -> m.getAccount().getId().equals(account.getId()))
                        .findFirst()
                        .orElse(null);

                if (member != null) {
                    if (!member.getIsOwner()) {
                        int deletedEntries = accountGroupRepository.deleteMemberFromGroup(groupUuid, account.getUuid());
                        if (deletedEntries == 0) {
                            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to leave group");
                        } else {
                            return null;
                        }
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner cannot leave the group");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is not a member of the group");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
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
                    accountGroupInvitationAccountRepository.delete(invitation);
                } else {
                    invitation.setStatus(status);
                    accountGroupInvitationAccountRepository.save(invitation);
                }

            } else {
                throw new RuntimeException("Invitation already answered");
            }
        } else {
            throw new RuntimeException("Invitation not found");
        }
        return null;
    }

    private void checkPermission(AccountGroup group, Account account, String permission) {
        // permission: "admin", "owner"

        Boolean isAdmin = group.getMembers().stream()
                .filter(member -> member.getAccount().getId().equals(account.getId()))
                .anyMatch(member -> (member.getIsAdmin() != null && member.getIsAdmin()));
        Boolean isOwner = group.getMembers().stream()
                .filter(member -> member.getAccount().getId().equals(account.getId()))
                .anyMatch(member -> (member.getIsOwner() != null && member.getIsOwner()));

        if (permission.equals("admin") && !isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else if (permission.equals("owner") && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private Boolean isGroupMember(AccountGroup group, Account account) {
        if (account == null) {
            return false;
        }
        return group.getMembers().stream()
                .filter(member -> member.getAccount().getId().equals(account.getId()))
                .findFirst()
                .orElse(null) != null;
    }

    private Boolean isGroupInvited(AccountGroup group, Account account) {
        return group.getInvitations().stream()
                .filter(invitation ->
                        invitation.getAccount().getId().equals(account.getId()) &&
                        invitation.getStatus() != InvitationStatusAccountEnum.REJECTED
                )
                .findFirst()
                .orElse(null) != null;
    }
}
