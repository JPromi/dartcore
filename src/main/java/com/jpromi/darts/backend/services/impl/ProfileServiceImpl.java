package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.mapper.ProfileResponseMapper;
import com.jpromi.darts.backend.models.ProfileResponse;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.services.ProfileService;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UrlService urlService;

    @Autowired
    private ProfileResponseMapper profileResponseMapper;

    @Override
    public ProfileResponse getProfile(String username) {
        Account account = accountRepository.findByUsername(username);
        if (account != null && account.getProfile() != null) {
            return profileResponseMapper.fromAccount(account);
        } else {
            return null;
        }
    }

    @Override
    public ProfileResponse getProfile(String username, String token) {
        // Implement the logic to retrieve the profile by username and token
        return null;
    }

}
