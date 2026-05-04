package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.Profile;
import com.jpromi.darts.backend.models.FileResponse;
import com.jpromi.darts.backend.models.ProfileResponse;
import com.jpromi.darts.backend.models.SettingProfileResponse;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingProfileResponseMapper {

    @Autowired
    private FileResponseMapper fileResponseMapper;

    public SettingProfileResponse fromAccount(Account account) {
        Profile profile = account.getProfile();
        SettingProfileResponse settingProfileResponse = SettingProfileResponse.builder()
                .uuid(account.getUuid())
                .username(account.getUsername())
                .description(profile.getDescription())
                .country(profile.getCountry())
                .avatar(fileResponseMapper.fromFile(account.getAvatar()))
                .banner(fileResponseMapper.fromFile(profile.getBanner()))
                .visibility(profile.getVisibility())
                .build();

        ProfileResponse.Links links = new ProfileResponse.Links();
        links.setX(profile.getLinkX());
        links.setInstagram(profile.getLinkInstagram());
        links.setFacebook(profile.getLinkFacebook());
        links.setYoutube(profile.getLinkYoutube());
        links.setGithub(profile.getLinkGithub());
        links.setTwitch(profile.getLinkTwitch());
        links.setWeb(profile.getLinkWeb());

        settingProfileResponse.setLinks(links);

        return settingProfileResponse;
    }
}
