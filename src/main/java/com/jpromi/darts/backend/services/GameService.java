package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.models.GameThrowRequest;
import com.jpromi.darts.backend.models.NewGameRequest;

import java.util.UUID;

public interface GameService {
    DartGame newGame(NewGameRequest newGameRequest, Account account);
    DartGame getGameByUuid(UUID uuid);
    Void addThrow(UUID gameUuid, GameThrowRequest request);
}
