package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.GroupResponse;

import java.util.List;

public interface GroupService {
    List<GroupResponse> getGroupsByAccount(Long accountId);
    List<GroupResponse> getGroupsByAccount(Account account);
}
