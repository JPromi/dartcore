package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.ProfileLightResponse;
import com.jpromi.darts.backend.models.ProfileResponse;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileLightResponseMapper {

    @Autowired
    private UrlService urlService;

    public ProfileLightResponse fromAccount(Account account) {
        return ProfileLightResponse.builder()
                .uuid(account.getUuid())
                .username(account.getUsername())
                .avatar(urlService.toPublicUrl(account.getAvatar(), "/static/files/placeholder/user.svg"))
                .visibility(account.getProfile().getVisibility())
                .build();
    }

}
