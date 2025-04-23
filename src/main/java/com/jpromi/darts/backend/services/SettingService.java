package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.SettingAccountRequest;
import com.jpromi.darts.backend.models.SettingAccountResponse;
import com.jpromi.darts.backend.models.SettingProfileResponse;

public interface SettingService {
    SettingProfileResponse getProfile(Account account);
    SettingProfileResponse updateProfile(Account account, SettingProfileResponse profile);
    SettingAccountResponse getAccount(Account account);
    SettingAccountResponse updateAccount(Account account, SettingAccountRequest request);
}
