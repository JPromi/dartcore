package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.models.GameResponse;
// ...existing code...
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
                .isCancelled(dartGame.getIsCancelled())
                .build();

        // set times after build to avoid Lombok builder type mismatch in some IDE inspections
        response.setStartTime(dartGame.getStartTime());
        response.setEndTime(dartGame.getEndTime());

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
