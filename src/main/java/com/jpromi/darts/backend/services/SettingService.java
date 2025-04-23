package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.SettingProfileResponse;

public interface SettingService {
    SettingProfileResponse getProfile(Account account);
    SettingProfileResponse updateProfile(Account account, SettingProfileResponse profile);
}
