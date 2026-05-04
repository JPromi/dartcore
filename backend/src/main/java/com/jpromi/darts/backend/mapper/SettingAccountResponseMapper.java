package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.SettingAccountResponse;
import org.springframework.stereotype.Component;

@Component
public class SettingAccountResponseMapper {

    public SettingAccountResponse fromAccount(Account account) {
        return SettingAccountResponse.builder()
                .uuid(account.getUuid())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .email(account.getEmail())
                .build();
    }
}
