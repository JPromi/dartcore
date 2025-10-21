package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.*;
import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.GameTypeEnum;
import com.jpromi.darts.backend.enums.ThrowType;
import com.jpromi.darts.backend.mapper.GamePlayerResponseMapper;
import com.jpromi.darts.backend.models.GameResponse;
import com.jpromi.darts.backend.models.GameThrowRequest;
import com.jpromi.darts.backend.models.NewGamePlayerRequest;
import com.jpromi.darts.backend.models.NewGameRequest;
import com.jpromi.darts.backend.repositories.*;
import com.jpromi.darts.backend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Autowired
    private GamePlayerResponseMapper gamePlayerResponseMapper;

    @Autowired
    private TmpGamePlayerStatsRepository tmpGamePlayerStatsRepository;

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

    @Override
    @Transactional(readOnly = true)
    public GameResponse getGameResponseByUuid(UUID gameUuid) {
        if (gameUuid != null) {
            DartGame game = this.dartGameRepository.findByUuid(gameUuid).orElse(null);

            if (game != null) {
                GameResponse response = GameResponse.builder()
                        .uuid(game.getUuid())
                        .startTime(game.getStartTime())
                        .endTime(game.getEndTime())
                        .gameType(game.getGameType())
                        .gameTypeClassicInType(game.getGameTypeClassicInType())
                        .gameTypeClassicOutType(game.getGameTypeClassicOutType())
                        .gameTypeClassicPoints(game.getGameTypeClassicPoints())
                        .players(new ArrayList<>())
                        .round(game.getThrowsList().isEmpty() ? 0 : getThrowRound(game.getGameType(), game.getPlayers(), dartThrowRepository.findByGameAndIsUndoFalse(game)))
                        .build();

                Integer roundSize = getRoundSize(game.getGameType());

                List<DartThrow> dartThrowsReversed = dartThrowRepository.findByGameAndIsUndoFalse(game).reversed();

                // player
                // Hibernate.initialize(game.getPlayers());
                for (DartPlayer player : game.getPlayers()) {
                    GameResponse.GamePlayerResponse playerResponse = gamePlayerResponseMapper.fromDartPlayer(player);
                    List<GameResponse.GamePlayerResponse.GameThrowResponse> throwsResponses = new ArrayList<>();
                    // get tmp stats
                    Optional<TmpGamePlayerStats> statsOpt = tmpGamePlayerStatsRepository.findByPlayerId(player.getId());
                    if (statsOpt.isPresent()) {
                        TmpGamePlayerStats stats = statsOpt.get();
                        playerResponse.setScore(stats.getTotalScore());
                        playerResponse.setHighscore(stats.getHighscore());
                    } else {
                        playerResponse.setScore(game.getGameTypeClassicPoints());
                        playerResponse.setHighscore(0L);
                    }

                    // get last throws
                    Integer foundInRound = null;
                    Boolean notCountableFound = false;
                    for (DartThrow dartThrow : dartThrowsReversed) {
                        if (dartThrow.getPlayer().getId().equals(player.getId())) {
                            if (foundInRound == null) {
                                foundInRound = dartThrow.getRound();
                            } else if (!foundInRound.equals(dartThrow.getRound())) {
                                break; // we have all throws for the last round
                            }

                            if (Boolean.TRUE.equals(dartThrow.getIsNotCountable())) {
                                notCountableFound = true;
                            }

                            throwsResponses.add(GameResponse.GamePlayerResponse.GameThrowResponse.builder()
                                    .type(dartThrow.getType())
                                    .multiplier(dartThrow.getMultiplier())
                                    .score(dartThrow.getScore())
                                    .timestamp(dartThrow.getTimestamp())
                                    .round(dartThrow.getRound())
                                    .build()
                            );
                        }
                    }

                    Collections.reverse(throwsResponses);

                    if (notCountableFound) {

                        if (throwsResponses.size() < roundSize) {
                            // fill up with not countable throws
                            for (int i = throwsResponses.size(); i < roundSize; i++) {
                                throwsResponses.add(GameResponse.GamePlayerResponse.GameThrowResponse.builder()
                                        .type(ThrowType.ABORT)
                                        .multiplier(null)
                                        .score(0)
                                        .timestamp(null)
                                        .round(foundInRound)
                                        .build()
                                );
                            }
                        }
                    }

                    playerResponse.setIsCurrentPlayer(getCurrentPlayer(game.getPlayers(), dartThrowRepository.findByGameAndIsUndoFalse(game), getRoundSize(game.getGameType())).getId().equals(player.getId()));

                    if(!(throwsResponses.size() >= roundSize && playerResponse.getIsCurrentPlayer())) {
                        playerResponse.setThrowList(throwsResponses);
                    }

                    response.getPlayers().add(playerResponse);
                }

                return response;
            } else {
                throw new IllegalArgumentException("Game not found for UUID: " + gameUuid);
            }
        } else {
            throw new IllegalArgumentException("Game UUID cannot be null or empty");
        }
    }

    @Transactional
    @Override
    public DartThrow addThrow(UUID gameUuid, GameThrowRequest request) {
        DartGame game = this.getGameByUuid(gameUuid);
        if (game != null && request != null && game.getEndTime() == null) {
            if (request.getIsUndo()) {
                List<DartThrow> gameThrowsActive = dartThrowRepository.findByGameAndIsUndoFalseForUpdate(game);

                if (!gameThrowsActive.isEmpty()) {
                    DartThrow lastThrow = gameThrowsActive.getLast();
                    lastThrow.setIsUndo(true);

                    // update tmp player stats
                    Optional<TmpGamePlayerStats> playerStats = tmpGamePlayerStatsRepository.findByPlayerId(lastThrow.getPlayer().getId());

                    if (playerStats.isPresent() && !Boolean.TRUE.equals(lastThrow.getIsNotCountable())) {
                        TmpGamePlayerStats stats = playerStats.get();
                        stats.setTotalScore(stats.getTotalScore() + calculatePoints(lastThrow.getScore(), lastThrow.getMultiplier()));
                        tmpGamePlayerStatsRepository.save(stats);
                    }

                    dartThrowRepository.save(lastThrow);
                }
            } else {
                List<DartThrow> gameThrowsActive = dartThrowRepository.findByGameAndIsUndoFalseForUpdate(game);

                DartThrow dartThrow = DartThrow.builder()
                        .player(getCurrentPlayer(game.getPlayers(), gameThrowsActive, getRoundSize(game.getGameType()))) // fix this
                        .game(game)
                        .type(request.getType())
                        .round(getThrowRound(game.getGameType(), game.getPlayers(), gameThrowsActive))
                        .multiplier(request.getMultiplier())
                        .score(request.getPoint())
                        .build();

                // get player stats
                TmpGamePlayerStats playerStats = tmpGamePlayerStatsRepository.findByPlayerId(dartThrow.getPlayer().getId()).orElse(
                        TmpGamePlayerStats.builder()
                                .playerId(dartThrow.getPlayer().getId())
                                .gameId(dartThrow.getGame().getId())
                                .totalScore(game.getGameTypeClassicPoints())
                                .highscore(0L)
                                .build()
                );

                // check if throw counts
                switch (game.getGameType()) {
                    case CLASSIC:
                        // check in type
                        if (dartThrow.getRound().equals(0) && gameThrowsActive.isEmpty()) {
                            if (!(game.getGameTypeClassicInType() == null || dartThrow.getMultiplier().equals(game.getGameTypeClassicInType()))) {
                                dartThrow.setIsNotCountable(true);
                            }
                        }

                        // check score
                        Long newScore = playerStats.getTotalScore() - calculatePoints(dartThrow.getScore(), dartThrow.getMultiplier());

                        if (newScore < 0) {
                            dartThrow.setIsNotCountable(true);
                        } else if (newScore.equals(0)) {
                            // check out type
                            if (!(game.getGameTypeClassicOutType().describeConstable().isEmpty() || dartThrow.getMultiplier().equals(game.getGameTypeClassicOutType()))) {
                                dartThrow.setIsNotCountable(true);
                            }
                        }

                        if (
                            game.getGameTypeClassicOutType() != null && (
                                (game.getGameTypeClassicOutType().equals(DartThrowMultiplierEnum.TRIPLE) && newScore < 3) ||
                                (game.getGameTypeClassicOutType().equals(DartThrowMultiplierEnum.DOUBLE) && newScore < 2)
                            )
                        ) {
                            dartThrow.setIsNotCountable(true);
                        }

                        if (
                            (game.getGameTypeClassicOutType() == null && newScore.equals(0L)) ||
                            (game.getGameTypeClassicOutType() != null && dartThrow.getMultiplier() != null && game.getGameTypeClassicOutType().equals(dartThrow.getMultiplier()) && newScore.equals(0L))
                        ) {
                            // winner
                            game.setEndTime(LocalDateTime.now());
                        }

                        if (!dartThrow.getIsNotCountable()) {
                            playerStats.setTotalScore(newScore);
                        }
                        break;
                }

                if (game.getEndTime() != null) {
                    dartGameRepository.save(game);
                }

                tmpGamePlayerStatsRepository.save(playerStats);
                return dartThrowRepository.save(dartThrow);
            }

        } else {
            throw new IllegalArgumentException("Game has already ended or invalid request");
        }
        throw new IllegalArgumentException("Game has already ended or invalid request");
    }

    @Override
    public GameResponse.GamePlayerResponse getPlayerResponse(Long dartPlayerId) {
        GameResponse.GamePlayerResponse response = GameResponse.GamePlayerResponse.builder().build();

        Optional<TmpGamePlayerStats> statsOpt = tmpGamePlayerStatsRepository.findByPlayerId(dartPlayerId);
        if (statsOpt.isPresent()) {
            TmpGamePlayerStats stats = statsOpt.get();
            response.setScore(stats.getTotalScore());
            response.setHighscore(stats.getHighscore());
        }

        return response;
    }

    private Integer getRoundSize(GameTypeEnum gameType) {
        switch (gameType) {
            default:
                return 3;
        }
    }

    private Integer getThrowRound(GameTypeEnum gameType,
                                  List<DartPlayer> players,
                                  List<DartThrow> gameThrowsActive) {
        if (gameThrowsActive == null || gameThrowsActive.isEmpty()) return 0;

        gameThrowsActive.sort(Comparator
                .comparing(DartThrow::getTimestamp).reversed());

        // get last active player
        Long lastActivePlayerId = null;
        for (int i = players.size() - 1; i >= 0; i--) {
            DartPlayer p = players.get(i);
            if (p.getLeftGameAt() == null) { lastActivePlayerId = p.getId(); break; }
        }
        if (lastActivePlayerId == null) return 0; // niemand aktiv

        // Current round and player
        DartThrow latest = gameThrowsActive.get(0);
        int roundSize = getRoundSize(gameType);
        int currentRound = latest.getRound();
        Long currentPlayerId = latest.getPlayer().getId();

        // count throws in current round for current player
        int throwCount = 0;
        for (DartThrow t : gameThrowsActive) {
            if (t.getRound() != currentRound) break;
            if (!t.getPlayer().getId().equals(currentPlayerId)) break;
            throwCount++;
            if (throwCount == roundSize) break;
        }

        // if current player has thrown all throws in this round and is last active player, next round
        if (throwCount == roundSize && currentPlayerId.equals(lastActivePlayerId)) {
            return currentRound + 1;
        }
        return currentRound;
    }

    private DartPlayer getCurrentPlayer(List<DartPlayer> players,
                                        List<DartThrow> gameThrowsActive,
                                        Integer roundSize) {

        if (gameThrowsActive.isEmpty()) {
            // first active player
            for (DartPlayer player : players) {
                if (player.getLeftGameAt() == null) return player;
            }
            throw new IllegalArgumentException("No active player found in this game");
        }

        Long lastPlayerId = null;
        int throwCount = 0;

        // reverse list to start from last throw
        Collections.reverse(gameThrowsActive);

        for (DartThrow dartThrow : gameThrowsActive) {
            if (lastPlayerId == null) {
                lastPlayerId = dartThrow.getPlayer().getId();
            }

            // If throw is not countable, skip
            if (Boolean.TRUE.equals(dartThrow.getIsNotCountable())) {
                throwCount = roundSize;
                break;
            }

            if (lastPlayerId.equals(dartThrow.getPlayer().getId())) {
                throwCount++;
                if (throwCount >= roundSize) break;
            } else {
                break;
            }
        }

        // samre player
        if (throwCount < roundSize) {
            for (DartPlayer player : players) {
                if (player.getId().equals(lastPlayerId)) return player;
            }
        } else {
            // next active player
            boolean returnNext = false;
            for (DartPlayer player : players) {
                if (returnNext && player.getLeftGameAt() == null) return player;
                if (player.getId().equals(lastPlayerId)) returnNext = true;
            }

            // if last player, return first active player
            for (DartPlayer player : players) {
                if (player.getLeftGameAt() == null) return player;
            }
        }

        throw new IllegalArgumentException("No active player found in this game");
    }


    private Integer calculatePoints(Integer points, DartThrowMultiplierEnum multiplier) {
        if (points == null || multiplier == null) return 0;
        switch (multiplier) {
            case SINGLE:
                return points;
            case DOUBLE:
                return points * 2;
            case TRIPLE:
                return points * 3;
            default:
                return points;
        }
    }
}
