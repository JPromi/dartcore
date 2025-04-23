package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.ProfileResponse;

public interface ProfileService {
    ProfileResponse getProfile(String username);
    ProfileResponse getProfile(String username, Account requestedAccount);
}
