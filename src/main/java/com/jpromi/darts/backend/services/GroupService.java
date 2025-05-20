package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.GroupLightResponse;
import com.jpromi.darts.backend.models.GroupRequest;
import com.jpromi.darts.backend.models.GroupResponse;
import com.jpromi.darts.backend.models.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    List<GroupLightResponse> getGroupsByAccount(Long accountId);
    List<GroupLightResponse> getGroupsByAccount(Account account);
    GroupResponse getGroupByUuid(UUID uuid, Account account);
    GroupResponse createGroup(GroupRequest groupRequest, Account account);
    PageResponse<GroupLightResponse> searchGroups(String query, Account account, Pageable pageable, Boolean isMember, Boolean isPublic);
}
