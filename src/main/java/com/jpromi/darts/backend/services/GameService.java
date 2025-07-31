package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.models.NewGameRequest;

public interface GameService {
    String newGame(NewGameRequest newGameRequest, Account account);
}
