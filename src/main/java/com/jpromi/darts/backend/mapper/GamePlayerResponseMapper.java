package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartPlayer;
import com.jpromi.darts.backend.models.GameResponse;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GamePlayerResponseMapper {

    @Autowired
    private UrlService urlService;

    public GameResponse.GamePlayerResponse fromDartPlayer(DartPlayer dartPlayer) {
        GameResponse.GamePlayerResponse response = GameResponse.GamePlayerResponse.builder()
                .build();

        // set Player name
        if (dartPlayer.getAccount() != null) {
            response.setAccountUuid(dartPlayer.getAccount().getUuid());
            response.setName(dartPlayer.getAccount().getUsername());
            response.setAvatarUrl(urlService.toPublicUrl(dartPlayer.getAccount().getAvatar()));
        } else {
            response.setName(dartPlayer.getGuestName());
        }

        return response;
    }

}
