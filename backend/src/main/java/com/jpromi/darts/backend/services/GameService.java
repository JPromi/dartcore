package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.Location;
import com.jpromi.darts.backend.models.GameResponse;
import com.jpromi.darts.backend.models.GameThrowRequest;
import com.jpromi.darts.backend.models.NewGameRequest;
import com.jpromi.darts.backend.models.NewGameLocationResponse;

import java.util.List;
import java.util.UUID;

public interface GameService {
    DartGame newGame(NewGameRequest newGameRequest, Account account);
    DartGame newGameForLocation(NewGameRequest newGameRequest, Location location);
    DartGame getGameByUuid(UUID uuid);
    DartGame endGame(UUID gameUuid);
    DartGame addThrow(UUID gameUuid, GameThrowRequest request);
    GameResponse getGameResponseByUuid(UUID gameUuid);
    GameResponse getGameResponseByUuid(DartGame game);
    List<GameResponse> getActiveGamesResponseByAccount(Account account);
    List<GameResponse> getActiveGamesResponseByLocation(Location location);
    GameResponse getLastGameResponseByLocation(Location location);
    List<NewGameLocationResponse> getLocationsForNewGame(UUID groupUuid, Account account);
}
