package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.models.NewGameRequest;
import com.jpromi.darts.backend.repositories.DartGameRepository;
import com.jpromi.darts.backend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private DartGameRepository dartGameRepository;

    @Override
    public String newGame(NewGameRequest newGameRequest, Account account) {
        if (newGameRequest != null && account != null) {
            DartGame dartGame = DartGame.builder()
                    .gameType(newGameRequest.getGameType())
                    .gameTypeClassicPoints(newGameRequest.getGameTypeClassicPoints())
                    .gameTypeClassicInType(newGameRequest.getGameTypeClassicInType())
                    .gameTypeClassicOutType(newGameRequest.getGameTypeClassicOutType())
//                    .creator(account)
//                    .location()
                    .build();
            return null;
        } else {
            throw new IllegalArgumentException("New game request or/and account cannot be null");
        }
    }
}
