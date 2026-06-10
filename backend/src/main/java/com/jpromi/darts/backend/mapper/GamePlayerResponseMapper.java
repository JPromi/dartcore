package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartHint;
import com.jpromi.darts.backend.entities.DartPlayer;
import com.jpromi.darts.backend.models.GameResponse;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            response.setAvatar(urlService.toPublicUrl(dartPlayer.getAccount().getAvatar(), "/static/files/placeholder/user.svg"));
            response.setOrderIndex(dartPlayer.getOrderIndex());
        } else {
            response.setName(dartPlayer.getGuestName());
            response.setOrderIndex(dartPlayer.getOrderIndex() != null ? dartPlayer.getOrderIndex() : 99);
            response.setAvatar(urlService.toPublicUrl(null, "/static/files/placeholder/user.svg"));
        }

        return response;
    }

    public List<GameResponse.GamePlayerResponse.GameHintResponse> hintResponseFromDartHint(DartHint hint) {
        List<GameResponse.GamePlayerResponse.GameHintResponse> hints = new java.util.ArrayList<>();

        if  (hint != null) {
            if (hint.getT1Points() != null) {
                hints.add(
                        GameResponse.GamePlayerResponse.GameHintResponse.builder()
                                .points(hint.getT1Points())
                                .multiplier(hint.getT1Multiplier())
                                .build()
                );
            }

            if (hint.getT2Points() != null) {
                hints.add(
                        GameResponse.GamePlayerResponse.GameHintResponse.builder()
                                .points(hint.getT2Points())
                                .multiplier(hint.getT2Multiplier())
                                .build()
                );
            }

            if (hint.getT3Points() != null) {
                hints.add(
                        GameResponse.GamePlayerResponse.GameHintResponse.builder()
                                .points(hint.getT3Points())
                                .multiplier(hint.getT3Multiplier())
                                .build()
                );
            }
        }

        return hints;
    }

}
