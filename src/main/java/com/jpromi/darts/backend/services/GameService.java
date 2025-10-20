package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartPlayer;
import com.jpromi.darts.backend.entities.DartThrow;
import com.jpromi.darts.backend.models.GameResponse;
import com.jpromi.darts.backend.models.GameThrowRequest;
import com.jpromi.darts.backend.models.NewGameRequest;

import java.util.List;
import java.util.UUID;

public interface GameService {
    DartGame newGame(NewGameRequest newGameRequest, Account account);
    DartGame getGameByUuid(UUID uuid);
    DartThrow addThrow(UUID gameUuid, GameThrowRequest request);
    GameResponse.GamePlayerResponse getPlayerResponse(Long dartPlayerId);
}
