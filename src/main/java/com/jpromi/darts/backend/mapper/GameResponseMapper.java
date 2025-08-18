package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.models.GameResponse;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameResponseMapper {

    @Autowired
    private GamePlayerResponseMapper gamePlayerResponseMapper;

    public GameResponse fromDartGame(DartGame dartGame) {
        GameResponse response = GameResponse.builder()
                .gameType(dartGame.getGameType())
                .uuid(dartGame.getUuid())
                .gameTypeClassicInType(dartGame.getGameTypeClassicInType())
                .gameTypeClassicOutType(dartGame.getGameTypeClassicOutType())
                .gameTypeClassicPoints(dartGame.getGameTypeClassicPoints())
                .groupUuid(dartGame.getGroup() != null ? dartGame.getGroup().getUuid() : null)
                .finishedAt(dartGame.getEndTime())
                .startedAt(dartGame.getStartTime())
                .isCancelled(dartGame.getIsCancelled())
                .build();

        // set Players
        if (dartGame.getPlayers() != null) {
            for (var dartPlayer : dartGame.getPlayers()) {
                GameResponse.GamePlayerResponse playerResponse = gamePlayerResponseMapper.fromDartPlayer(dartPlayer);
                response.getPlayers().add(playerResponse);
            }
        }

        return response;
    }

}
