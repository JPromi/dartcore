package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.PageResponse;
import com.jpromi.darts.backend.models.ProfileLightResponse;
import com.jpromi.darts.backend.models.ProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileService {
    ProfileResponse getProfile(String username);
    ProfileResponse getProfile(String username, Account requestedAccount);
    PageResponse<ProfileLightResponse> searchProfile(String query, Account viewer, boolean isPlayable, Pageable pageable);
}
