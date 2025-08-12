package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.Profile;
import com.jpromi.darts.backend.enums.ProfileVisibilityEnum;
import com.jpromi.darts.backend.mapper.PageResponseMapper;
import com.jpromi.darts.backend.mapper.ProfileLightResponseMapper;
import com.jpromi.darts.backend.mapper.ProfileResponseMapper;
import com.jpromi.darts.backend.models.PageResponse;
import com.jpromi.darts.backend.models.ProfileLightResponse;
import com.jpromi.darts.backend.models.ProfileResponse;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.services.ProfileService;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UrlService urlService;

    @Autowired
    private ProfileResponseMapper profileResponseMapper;

    @Autowired
    private ProfileLightResponseMapper profileLightResponseMapper;

    @Autowired
    private PageResponseMapper pageResponseMapper;

    @Override
    public ProfileResponse getProfile(String username) {
        return getProfile(username, null);
    }

    @Override
    public ProfileResponse getProfile(String username, Account requestedAccount) {
        Account account = accountRepository.findByUsername(username);
        if (account != null && account.getProfile() != null) {
            if(account.getProfile().getVisibility() != ProfileVisibilityEnum.PRIVATE || account.equals(requestedAccount)) {
                return profileResponseMapper.fromAccount(account);
            } else {
                return profileResponseMapper.fromAccountPrivate(account);
            }
        } else {
            return null;
        }
    }

    @Override
    public PageResponse<ProfileLightResponse> searchProfile(String query, Account viewer, boolean isPlayable, Pageable pageable) {
        Page<Account> accounts = accountRepository.searchByUsername(query, viewer != null ? viewer.getId() : null, isPlayable, pageable);

        Page<ProfileLightResponse> profileLigthPage = accounts.map(account -> {
            ProfileLightResponse profileLightResponse = profileLightResponseMapper.fromAccount(account);
            return profileLightResponse;
        });

        return pageResponseMapper.fromPage(profileLigthPage);
    }

}
