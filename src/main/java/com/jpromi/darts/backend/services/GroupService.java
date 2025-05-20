package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.GroupLightResponse;
import com.jpromi.darts.backend.models.GroupResponse;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    List<GroupLightResponse> getGroupsByAccount(Long accountId);
    List<GroupLightResponse> getGroupsByAccount(Account account);
    GroupResponse getGroupByUuid(UUID uuid, Account account);
}
