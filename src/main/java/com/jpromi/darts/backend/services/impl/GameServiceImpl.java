package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartPlayer;
import com.jpromi.darts.backend.entities.DartThrow;
import com.jpromi.darts.backend.enums.GameTypeEnum;
import com.jpromi.darts.backend.models.GameThrowRequest;
import com.jpromi.darts.backend.models.NewGamePlayerRequest;
import com.jpromi.darts.backend.models.NewGameRequest;
import com.jpromi.darts.backend.repositories.*;
import com.jpromi.darts.backend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private DartGameRepository dartGameRepository;

    @Autowired
    private DartThrowRepository dartThrowRepository;

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
                for (int i = 0; i < newGameRequest.getPlayers().size(); i++) {
                    NewGamePlayerRequest playerRequest = newGameRequest.getPlayers().get(i);
                    // if name isset
                    if (playerRequest.getName() != null ) {
                        // Player is guest
                        DartPlayer player = DartPlayer.builder()
                                .guestName(playerRequest.getName())
                                .game(dartGame)
                                .orderIndex(i)
                                .build();
                        dartGame.addPlayer(player);
                    } else if (playerRequest.getAccountUuid() != null) {
                        // Player has account
                        Account playerAccount = this.accountRepository.findByUuid(playerRequest.getAccountUuid());
                        if (playerAccount != null) {
                            DartPlayer player = DartPlayer.builder()
                                    .account(playerAccount)
                                    .game(dartGame)
                                    .orderIndex(i)
                                    .build();
                            dartGame.addPlayer(player);
                        } else {
                            throw new IllegalArgumentException("Player account not found for UUID: " + playerRequest.getAccountUuid());
                        }
                    }
                }
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

    @Transactional
    @Override
    public Void addThrow(UUID gameUuid, GameThrowRequest request) {
        DartGame game = this.getGameByUuid(gameUuid);
        if (game != null && request != null) {
            if (request.getIsUndo()) {
                List<DartThrow> gameThrowsActive = dartThrowRepository.findByGameAndIsUndoFalse(game);

                if (!gameThrowsActive.isEmpty()) {
                    DartThrow lastThrow = gameThrowsActive.getLast();
                    lastThrow.setIsUndo(true);
                    dartThrowRepository.save(lastThrow);
                }
            } else {
                List<DartThrow> gameThrowsActive = dartThrowRepository.findByGameAndIsUndoFalse(game);

                DartThrow dartThrow = DartThrow.builder()
                        .player(getCurrentPlayer(game.getPlayers(), gameThrowsActive, getRoundSize(game.getGameType()))) // fix this
                        .game(game)
                        .type(request.getType())
                        .round(getThrowRound(game.getGameType(), game.getPlayers(), gameThrowsActive))
                        .multiplier(request.getMultiplier())
                        .score(request.getPoint())
                        .build();

                dartThrowRepository.save(dartThrow);
            }

        } else {
            throw new IllegalArgumentException("Game or request cannot be null");
        }
        return null;
    }

    private Integer getRoundSize(GameTypeEnum gameType) {
        switch (gameType) {
            default:
                return 3;
        }
    }

    private Integer getThrowRound(GameTypeEnum gameType, List<DartPlayer> players, List<DartThrow> gameThrowsActive) {
        if (gameThrowsActive.isEmpty()) {
            return 0;
        } else {
            Integer roundSize = getRoundSize(gameType);
            Long lastActivePlayerId = null;

            // reverse list
            Collections.reverse(players);

            // search last active player in game
            for (DartPlayer dartPlayer : players) {
                if (dartPlayer.getLeftGameAt() == null) {
                    lastActivePlayerId = dartPlayer.getId();
                    break;
                }
            }

            // check throw
            Integer throwCount = 0;
            Integer round = 0;
            Long lastPlayerId = null;

            for (DartThrow dartThrow : gameThrowsActive) {
                System.out.println(dartThrow);
                if (lastPlayerId == null) {
                    lastPlayerId = dartThrow.getPlayer().getId();
                    throwCount += 1;
                } else {
                    if (lastPlayerId.equals(dartThrow.getPlayer().getId())) {
                        throwCount += 1;
                        round = dartThrow.getRound();

                        if (throwCount >= roundSize) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }

            if (throwCount >= roundSize) {
                System.out.println("Round is full");
                System.out.println(lastActivePlayerId.toString() + " == " + lastPlayerId.toString());
                if (lastActivePlayerId.equals(lastPlayerId)) {
                    System.out.println("Next round");
                    return round + 1;
                } else {
                    System.out.println("Same round");
                    return round;
                }
            } else {
                return round;
            }
        }
    }

    private DartPlayer getCurrentPlayer(List<DartPlayer> players, List<DartThrow> gameThrowsActive, Integer roundSize) {

        // set player
        if (gameThrowsActive.isEmpty()) {
            // find first player that not left game
            for (DartPlayer player : players) {
                if (player.getLeftGameAt() == null) {
                    return player;
                }
            }

            throw new IllegalArgumentException("No active player found in this game");
        } else {
            Long lastPlayerId = null;
            Integer throwCount = 0;

            // reverse list
            Collections.reverse(gameThrowsActive);

            // calculate throws
            for (DartThrow dartThrow : gameThrowsActive) {
                if (lastPlayerId == null) {
                    lastPlayerId = dartThrow.getPlayer().getId();
                    throwCount += 1;
                } else {
                    if (lastPlayerId.equals(dartThrow.getPlayer().getId())) {
                        throwCount += 1;

                        if (throwCount >= roundSize) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }

            // get player
            if (throwCount < roundSize) {
                // same player
                for (DartPlayer player : players) {
                    if (player.getId().equals(lastPlayerId)) {
                        return player;
                    }
                }
            } else {
                // next player
                Boolean returnNext = false;
                for (DartPlayer player : players) {
                    if (returnNext) {
                        if (player.getLeftGameAt() == null) {
                            return player;
                        }
                    }
                    if (player.getId().equals(lastPlayerId)) {
                        returnNext = true;
                    }
                }

                // if no next player, return first active player
                for (DartPlayer player : players) {
                    if (player.getLeftGameAt() == null) {
                        return player;
                    }
                }
            }

            throw  new IllegalArgumentException("No active player found in this game");
        }
    }
}
