package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.ProfileResponse;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileResponseMapper {

    @Autowired
    private UrlService urlService;

    public ProfileResponse fromAccount(Account account) {
        ProfileResponse profileResponse = ProfileResponse.builder()
                .uuid(account.getUuid())
                .username(account.getUsername())
                .description(account.getProfile().getDescription())
                .country(account.getProfile().getCountry())
                .age(account.getAge())
                .createdAt(account.getEmailVerificationTimestamp())
                .visibility(account.getProfile().getVisibility())
                .build();

        // img
        if (account.getAvatar() != null) {
            profileResponse.setAvatar(urlService.toPublicUrl(account.getAvatar().getRealPath()));
        } else {
            profileResponse.setAvatar(null);
        }

        if (account.getProfile().getBanner() != null) {
            profileResponse.setBanner(urlService.toPublicUrl(account.getProfile().getBanner().getRealPath()));
        } else {
            profileResponse.setBanner(null);
        }

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
    }

    public ProfileResponse fromAccountPrivate(Account account) {
        ProfileResponse profileResponse = ProfileResponse.builder()
                .uuid(account.getUuid())
                .username(account.getUsername())
                .description(null)
                .country(null)
                .avatar(urlService.toPublicUrl(account.getAvatar()))
                .banner(null)
                .createdAt(null)
                .visibility(account.getProfile().getVisibility())
                .build();

        ProfileResponse.Links links = new ProfileResponse.Links();

        profileResponse.setLinks(links);

        return profileResponse;
    }

}
