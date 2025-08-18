package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartPlayer;
import com.jpromi.darts.backend.enums.GameTypeEnum;
import com.jpromi.darts.backend.models.NewGamePlayerRequest;
import com.jpromi.darts.backend.models.NewGameRequest;
import com.jpromi.darts.backend.repositories.AccountGroupRepository;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.repositories.DartGameRepository;
import com.jpromi.darts.backend.repositories.DartPlayerRepository;
import com.jpromi.darts.backend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private DartGameRepository dartGameRepository;

    @Autowired
    private DartPlayerRepository dartPlayerRepository;

    @Autowired
    private AccountGroupRepository accountGroupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public DartGame newGame(NewGameRequest newGameRequest, Account account) {
        if (newGameRequest != null && account != null) {
            DartGame dartGame = DartGame.builder()
                    .gameType(newGameRequest.getGameType())
                    .creator(account)
                    .build();

            // set group
            if (newGameRequest.getGroupUuid() != null) {
                dartGame.setGroup(this.accountGroupRepository.findByUuid(newGameRequest.getGroupUuid()));
            }

            // set game settings based on game type
            switch (dartGame.getGameType()) {
                case CLASSIC:
                    dartGame.setGameTypeClassicPoints(newGameRequest.getGameTypeClassicPoints());
                    dartGame.setGameTypeClassicInType(newGameRequest.getGameTypeClassicInType());
                    dartGame.setGameTypeClassicOutType(newGameRequest.getGameTypeClassicOutType());
                    break;
            }

            // DartGame savedGame = this.dartGameRepository.save(dartGame);

            // set players
            if (newGameRequest.getPlayers() != null && !newGameRequest.getPlayers().isEmpty()) {
                newGameRequest.getPlayers().forEach(playerRequest -> {
                    System.out.println("Adding player: " + playerRequest);
                    // if name isset
                    if (playerRequest.getName() != null ) {
                        // Player is guest
                        DartPlayer player = DartPlayer.builder()
                                .guestName(playerRequest.getName())
                                .game(dartGame)
                                .build();
                        dartGame.addPlayer(player);
                    } else if (playerRequest.getAccountUuid() != null) {
                        // Player has account
                        Account playerAccount = this.accountRepository.findByUuid(playerRequest.getAccountUuid());
                        if (playerAccount != null) {
                            DartPlayer player = DartPlayer.builder()
                                    .account(playerAccount)
                                    .game(dartGame)
                                    .build();
                            dartGame.addPlayer(player);
                        } else {
                            throw new IllegalArgumentException("Player account not found for UUID: " + playerRequest.getAccountUuid());
                        }
                    }
                });
            }

            // save
            DartGame savedGame = this.dartGameRepository.save(dartGame);

            return savedGame;
        } else {
            throw new IllegalArgumentException("New game request or/and account cannot be null");
        }
    }

    @Override
    public DartGame getGameByUuid(UUID gameUuid) {
        if (gameUuid != null) {
            return this.dartGameRepository.findByUuid(gameUuid).orElse(null);
        } else {
            throw new IllegalArgumentException("Game UUID cannot be null or empty");
        }
    }
}
