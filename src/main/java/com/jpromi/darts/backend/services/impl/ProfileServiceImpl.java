package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
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

    @Override
    public ProfileResponse getProfile(String username) {
        Account account = accountRepository.findByUsername(username);
        if (account != null && account.getProfile() != null) {
            ProfileResponse profileResponse = ProfileResponse.builder()
                    .uuid(account.getUuid())
                    .username(account.getUsername())
                    .description(account.getProfile().getDescription())
                    .country(account.getProfile().getCountry())
                    .avatar(urlService.toPublicUrl(account.getAvatar().getRealPath()))
                    .banner(urlService.toPublicUrl(account.getProfile().getBanner().getRealPath()))
                    .createdAt(account.getEmailVerificationTimestamp())
                    .visibility(account.getProfile().getVisibility())
                    .build();

            ProfileResponse.Links links = new ProfileResponse.Links();
            links.setX(account.getProfile().getLinkX());
            links.setInstagram(account.getProfile().getLinkInstagram());
            links.setFacebook(account.getProfile().getLinkFacebook());
            links.setYoutube(account.getProfile().getLinkYoutube());
            links.setGithub(account.getProfile().getLinkGithub());
            links.setTwitch(account.getProfile().getLinkTwitch());
            links.setWeb(account.getProfile().getLinkWeb());

            profileResponse.setLinks(links);

            return profileResponse;
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
