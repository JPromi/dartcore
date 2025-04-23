package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.File;
import com.jpromi.darts.backend.mapper.SettingAccountResponseMapper;
import com.jpromi.darts.backend.mapper.SettingProfileResponseMapper;
import com.jpromi.darts.backend.models.SettingAccountRequest;
import com.jpromi.darts.backend.models.SettingAccountResponse;
import com.jpromi.darts.backend.models.SettingProfileResponse;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.services.FileService;
import com.jpromi.darts.backend.services.SettingService;
import com.jpromi.darts.backend.services.UrlService;
import com.password4j.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private UrlService urlService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private SettingProfileResponseMapper settingProfileResponseMapper;

    @Autowired
    private SettingAccountResponseMapper settingAccountResponseMapper;

    @Override
    public SettingProfileResponse getProfile(Account account) {
        return settingProfileResponseMapper.fromAccount(account);
    }

    @Override
    public SettingProfileResponse updateProfile(Account account, SettingProfileResponse profile) {
        account.setUsername(profile.getUsername());
        account.getProfile().setDescription(profile.getDescription());
        account.getProfile().setCountry(profile.getCountry());
        account.getProfile().setVisibility(profile.getVisibility());
        account.getProfile().setLinkX(profile.getLinks().getX());
        account.getProfile().setLinkInstagram(profile.getLinks().getInstagram());
        account.getProfile().setLinkFacebook(profile.getLinks().getFacebook());
        account.getProfile().setLinkYoutube(profile.getLinks().getYoutube());
        account.getProfile().setLinkGithub(profile.getLinks().getGithub());
        account.getProfile().setLinkTwitch(profile.getLinks().getTwitch());
        account.getProfile().setLinkWeb(profile.getLinks().getWeb());

        if(profile.getAvatar() != null) {
            System.out.println("Avatar UUID: " + profile.getAvatar().getUuid());
            File avatar = fileService.getFileByUuid(profile.getAvatar().getUuid());
            if(avatar != null) {
                avatar.setIsTmporary(false);
                account.setAvatar(avatar);
            }
        } else {
            if(account.getAvatar() != null) {
                fileService.deleteFile(account.getAvatar().getUuid());
            }
            account.setAvatar(null);
        }

        if(profile.getBanner() != null) {
            File banner = fileService.getFileByUuid(profile.getBanner().getUuid());
            if(banner != null) {
                banner.setIsTmporary(false);
                account.getProfile().setBanner(banner);
            }
        } else {
            if(account.getProfile().getBanner() != null) {
                fileService.deleteFile(account.getProfile().getBanner().getUuid());
            }
            account.getProfile().setBanner(null);
        }
        accountRepository.save(account);
        return profile;
    }

    @Override
    public SettingAccountResponse getAccount(Account account) {
        return settingAccountResponseMapper.fromAccount(account);
    }

    @Override
    public SettingAccountResponse updateAccount(Account account, SettingAccountRequest request) {
        account.setEmail(request.getEmail());
        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        if (request.getPasswordOld() != null && !request.getPasswordOld().isEmpty()) {
            if(Password.check(request.getPasswordOld(), account.getPassword()).withArgon2()) {
                if (request.getPasswordNew() != null && !request.getPasswordNew().isEmpty()) {
                    if(request.getPasswordNew().equals(request.getPasswordNewConfirm())) {
                        String password = Password.hash(request.getPasswordNew()).withArgon2().getResult();
                        account.setPassword(password);
                    } else {
                        throw new RuntimeException("New password and confirmation do not match");
                    }
                }

            } else {
                throw new RuntimeException("Old password is incorrect");
            }
        }
        accountRepository.save(account);
        return settingAccountResponseMapper.fromAccount(account);
    }
}
