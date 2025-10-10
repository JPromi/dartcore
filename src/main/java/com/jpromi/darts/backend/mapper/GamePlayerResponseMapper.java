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
                .playerUuid(dartPlayer.getUuid())
                .build();

        // set Player name
        if (dartPlayer.getAccount() != null) {
            response.setName(dartPlayer.getAccount().getUsername());
            response.setAvatar(urlService.toPublicUrl(dartPlayer.getAccount().getAvatar()));
            response.setOrderIndex(dartPlayer.getOrderIndex());
        } else {
            response.setName(dartPlayer.getGuestName());
            response.setOrderIndex(dartPlayer.getOrderIndex() != null ? dartPlayer.getOrderIndex() : 99);
        }

        return response;
    }

}
