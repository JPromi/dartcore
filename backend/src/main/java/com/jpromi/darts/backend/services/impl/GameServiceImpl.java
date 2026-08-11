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
import com.jpromi.darts.backend.models.NewGameLocationResponse;
import com.jpromi.darts.backend.repositories.*;
import com.jpromi.darts.backend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private DartGameRepository dartGameRepository;

    @Autowired
    private DartThrowRepository dartThrowRepository;

    @Autowired
    private AccountGroupRepository accountGroupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AccountGroupMemberRepository accountGroupMemberRepository;

    @Autowired
    private GamePlayerResponseMapper gamePlayerResponseMapper;

    @Autowired
    private TmpGamePlayerStatsRepository tmpGamePlayerStatsRepository;

    @Autowired
    private DartHintRepository dartHintRepository;

    @Override
    @Transactional
    public DartGame newGame(NewGameRequest newGameRequest, Account account) {
        if (newGameRequest != null && account != null) {
            DartGame dartGame = DartGame.builder()
                    .gameType(newGameRequest.getGameType())
                    .creator(account)
                    .build();

            // set group
            if (newGameRequest.getGroupUuid() != null) {
                AccountGroup group = this.accountGroupRepository.findByUuid(newGameRequest.getGroupUuid());
                if (group == null || !accountGroupMemberRepository.existsByAccountAndAccountGroup(account, group)) {
                    throw new IllegalArgumentException("Group not found or account is not a member");
                }
                dartGame.setGroup(group);

                if (newGameRequest.getLocationUuid() != null) {
                    Location location = locationRepository
                            .findByUuidAndGroupForUpdate(newGameRequest.getLocationUuid(), group)
                            .orElseThrow(() -> new IllegalArgumentException("Location not found in selected group"));
                    if (!dartGameRepository.findActiveGameIdsByLocationId(location.getId()).isEmpty()) {
                        throw new IllegalArgumentException("Location already has an active game");
                    }
                    dartGame.setLocation(location);
                }
            } else if (newGameRequest.getLocationUuid() != null) {
                throw new IllegalArgumentException("A location can only be selected together with its group");
            }

            // set game settings based on game type
            switch (dartGame.getGameType()) {
                case CLASSIC:
                    dartGame.setGameTypeClassicPoints(newGameRequest.getGameTypeClassicPoints());
                    dartGame.setGameTypeClassicInType(newGameRequest.getGameTypeClassicIn());
                    dartGame.setGameTypeClassicOutType(newGameRequest.getGameTypeClassicOut());
                    break;
            }

             // set players
            if (newGameRequest.getPlayers() != null && !newGameRequest.getPlayers().isEmpty()) {
                for (int i = 0; i < newGameRequest.getPlayers().size(); i++) {
                    NewGamePlayerRequest playerRequest = newGameRequest.getPlayers().get(i);
                    if (playerRequest.getName() != null) {
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

            DartGame savedGame = this.dartGameRepository.save(dartGame);
            return savedGame;
        } else {
            throw new IllegalArgumentException("New game request or/and account cannot be null");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewGameLocationResponse> getLocationsForNewGame(UUID groupUuid, Account account) {
        if (groupUuid == null || account == null) {
            throw new IllegalArgumentException("Group UUID and account are required");
        }
        AccountGroup group = accountGroupRepository.findByUuid(groupUuid);
        if (group == null || !accountGroupMemberRepository.existsByAccountAndAccountGroup(account, group)) {
            throw new IllegalArgumentException("Group not found or account is not a member");
        }

        return locationRepository.findByGroup(group).stream().map(location -> {
            List<DartGame> activeGames = dartGameRepository.findActiveGameIdsByLocationId(location.getId());
            DartGame activeGame = activeGames.isEmpty() ? null : activeGames.getFirst();
            return NewGameLocationResponse.builder()
                    .uuid(location.getUuid())
                    .name(location.getName())
                    .description(location.getDescription())
                    .address(location.getAddress())
                    .occupied(activeGame != null)
                    .activeGameUuid(activeGame != null ? activeGame.getUuid() : null)
                    .build();
        }).toList();
    }

    @Override
    public DartGame getGameByUuid(UUID gameUuid) {
        if (gameUuid != null) {
            return this.dartGameRepository.findByUuidWithPlayersAndAccounts(gameUuid).orElse(null);
        } else {
            throw new IllegalArgumentException("Game UUID cannot be null or empty");
        }
    }

    @Override
    @Transactional
    public DartGame endGame(UUID gameUuid) {
        if (gameUuid == null) {
            throw new IllegalArgumentException("Game UUID cannot be null or empty");
        }

        DartGame game = this.getGameByUuid(gameUuid);
        if (game == null) {
            return null;
        }

            if (game.getEndTime() == null) {
            game.setEndTime(Instant.now());
            game.setIsCancelled(false);
            this.dartGameRepository.save(game);
        }

        return game;
    }

    @Override
    @Transactional(readOnly = true)
    public GameResponse getGameResponseByUuid(UUID gameUuid) {
        DartGame game = this.dartGameRepository.findByUuidWithPlayersAndAccounts(gameUuid).orElse(null);
        return this.getGameResponseByUuid(game);
    }

    @Override
    @Transactional(readOnly = true)
    public GameResponse getGameResponseByUuid(DartGame game) {
        if (game != null) {
            // Fetch all active throws with their player associations eagerly loaded (join fetch).
            // This ensures DartPlayer entities are fully hydrated in the Hibernate session before
            // game.getPlayers() is accessed, preventing the loadedState=null NPE in Hibernate 6
            // that occurs when a lazy @ManyToOne proxy (loadedState=null) is encountered during
            // finalizeCollectionLoading of the players collection.
            // The query returns rows ordered by id, so no additional sort is needed.
            List<DartThrow> allActiveThrows = dartThrowRepository.findByGameAndIsUndoFalseWithPlayer(game);

            Integer roundSize = getRoundSize(game.getGameType());

            // Pre-group throws by player id so each player's list is already in chronological order
            Map<Long, List<DartThrow>> throwsByPlayerId = new HashMap<>();
            for (DartThrow t : allActiveThrows) {
                throwsByPlayerId
                        .computeIfAbsent(t.getPlayer().getId(), k -> new ArrayList<>())
                        .add(t);
            }

            GameResponse response = GameResponse.builder()
                    .uuid(game.getUuid())
                    .startTime(game.getStartTime())
                    .endTime(game.getEndTime())
                    .gameType(game.getGameType())
                    .gameTypeClassicInType(game.getGameTypeClassicInType())
                    .gameTypeClassicOutType(game.getGameTypeClassicOutType())
                    .gameTypeClassicPoints(game.getGameTypeClassicPoints())
                    .players(new ArrayList<>())
                    .round(allActiveThrows.isEmpty() ? 0 : getThrowRound(game.getGameType(), game.getPlayers(), allActiveThrows))
                    .build();

            // Determine current player once outside the loop
            Long currentPlayerId = null;
            if (game.getEndTime() == null) {
                try {
                    DartPlayer currentPlayer = getCurrentPlayer(game.getPlayers(), allActiveThrows, roundSize);
                    if (currentPlayer != null) {
                        currentPlayerId = currentPlayer.getId();
                    }
                } catch (IllegalArgumentException ignored) {
                    // No active player left in an ongoing game: keep response valid without a current player.
                    currentPlayerId = null;
                }
            }

            for (DartPlayer player : game.getPlayers()) {
                GameResponse.GamePlayerResponse playerResponse = gamePlayerResponseMapper.fromDartPlayer(player);

                if (player.getLeftGameAt() != null) {
                    playerResponse.setIsEliminated(true);
                }

                if (Boolean.TRUE.equals(player.getIsWinner())) {
                    playerResponse.setIsWinner(true);
                }

                // Remaining score from stats (updated incrementally on each throw)
                Optional<TmpGamePlayerStats> statsOpt = tmpGamePlayerStatsRepository.findByPlayerId(player.getId());
                playerResponse.setScore(statsOpt.isPresent()
                        ? statsOpt.get().getTotalScore()
                        : game.getGameTypeClassicPoints());

                boolean isCurrentPlayer = player.getId().equals(currentPlayerId);
                playerResponse.setIsCurrentPlayer(isCurrentPlayer);

                // Build throw list from this player's most recent round
                List<DartThrow> playerThrows = throwsByPlayerId.getOrDefault(player.getId(), Collections.emptyList());
                List<GameResponse.GamePlayerResponse.GameThrowResponse> throwsResponses =
                        buildLastRoundThrowResponses(playerThrows, roundSize);

                // Clear if the current player has a complete round — they are about to start a new round
                if (!(throwsResponses.size() >= roundSize && isCurrentPlayer)) {
                    playerResponse.setThrowList(throwsResponses);
                }

                // Highscore and average derived from actual throw history (correct after undos too)
                playerResponse.setHighscore(computeHighscore(playerThrows));
                playerResponse.setAverage(computeAverage(playerThrows));

                // get hint
                if (game.getGameType() == GameTypeEnum.CLASSIC) {
                    DartHint hint = dartHintRepository.findClassicHint(
                            game.getGameTypeClassicOutType(),
                            playerResponse.getScore().intValue()
                    ).orElse(null);

                    playerResponse.setHints(gamePlayerResponseMapper.hintResponseFromDartHint(hint));
                }

                response.getPlayers().add(playerResponse);
            }

            return response;
        } else {
            throw new IllegalArgumentException("Game not found for UUID");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GameResponse> getActiveGamesResponseByAccount(Account account) {
        if (account == null || account.getId() == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        List<DartGame> activeGames = this.dartGameRepository.findActiveGamesByAccountId(account.getId());
        if (activeGames == null || activeGames.isEmpty()) {
            return new ArrayList<>();
        }

        List<GameResponse> responses = new ArrayList<>(activeGames.size());
        for (DartGame game : activeGames) {
            responses.add(this.getGameResponseByUuid(game));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameResponse> getActiveGamesResponseByLocation(com.jpromi.darts.backend.entities.Location location) {
        if (location == null || location.getId() == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        List<DartGame> activeGames = this.dartGameRepository.findActiveGamesByLocationId(location.getId());
        if (activeGames == null || activeGames.isEmpty()) {
            return new ArrayList<>();
        }

        List<GameResponse> responses = new ArrayList<>(activeGames.size());
        for (DartGame game : activeGames) {
            responses.add(this.getGameResponseByUuid(game));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public GameResponse getLastGameResponseByLocation(com.jpromi.darts.backend.entities.Location location) {
        if (location == null || location.getId() == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }

        List<DartGame> games = this.dartGameRepository.findGamesByLocationIdOrderByStartTimeDesc(location.getId());
        if (games == null || games.isEmpty()) {
            return null;
        }

        return this.getGameResponseByUuid(games.getFirst());
    }

    /**
     * Builds the throw-response list for a player's most recent round.
     *
     * @param playerThrows sorted ascending by id (chronological order)
     * @param roundSize    number of darts per round for this game type
     */
    private List<GameResponse.GamePlayerResponse.GameThrowResponse> buildLastRoundThrowResponses(
            List<DartThrow> playerThrows, int roundSize) {

        if (playerThrows.isEmpty()) {
            return new ArrayList<>();
        }

        // The last saved throw (highest id) determines the player's current round
        int lastRound = playerThrows.getLast().getRound();

        boolean notCountableFound = false;
        List<GameResponse.GamePlayerResponse.GameThrowResponse> responses = new ArrayList<>();

        for (DartThrow t : playerThrows) {
            if (t.getRound() == lastRound) {
                if (Boolean.TRUE.equals(t.getIsNotCountable())) {
                    notCountableFound = true;
                }
                responses.add(GameResponse.GamePlayerResponse.GameThrowResponse.builder()
                        .type(t.getType())
                        .multiplier(t.getMultiplier())
                        .score(t.getScore())
                        .timestamp(t.getTimestamp())
                        .round(t.getRound())
                        .build());
            }
        }

        // Fill remaining slots with ABORT when the round ended early (bust / bad in-out type)
        if (notCountableFound && responses.size() < roundSize) {
            for (int i = responses.size(); i < roundSize; i++) {
                responses.add(GameResponse.GamePlayerResponse.GameThrowResponse.builder()
                        .type(ThrowType.ABORT)
                        .multiplier(null)
                        .score(0)
                        .timestamp(null)
                        .round(lastRound)
                        .build());
            }
        }

        return responses;
    }

    @Transactional
    @Override
    public DartGame addThrow(UUID gameUuid, GameThrowRequest request) {
        DartGame game = this.getGameByUuid(gameUuid);
        if (game != null && request != null && game.getEndTime() == null) {
            if (Boolean.TRUE.equals(request.getIsUndo())) {
                List<DartThrow> gameThrowsActive = dartThrowRepository.findByGameAndIsUndoFalseForUpdate(game);

                if (!gameThrowsActive.isEmpty()) {
                    gameThrowsActive.sort(Comparator.comparing(DartThrow::getId));
                    DartThrow lastThrow = gameThrowsActive.getLast();
                    lastThrow.setIsUndo(true);

                    // If the undone throw is not-countable and was a bust that voided earlier
                    // throws in the same round, restore those throws and re-deduct their scores.
                    if (Boolean.TRUE.equals(lastThrow.getIsNotCountable())) {
                        int lastRound = lastThrow.getRound();
                        Long lastPlayerId = lastThrow.getPlayer().getId();
                        List<DartThrow> bustVoided = gameThrowsActive.stream()
                                .filter(t -> !t.getId().equals(lastThrow.getId())
                                        && t.getRound() == lastRound
                                        && t.getPlayer().getId().equals(lastPlayerId)
                                        && Boolean.TRUE.equals(t.getIsNotCountable()))
                                .toList();

                        if (!bustVoided.isEmpty()) {
                            long scoreToReApply = 0;
                            for (DartThrow t : bustVoided) {
                                t.setIsNotCountable(false);
                                scoreToReApply += calculatePoints(t.getScore(), t.getMultiplier());
                                // managed entity — dirty checking persists the flag at commit
                            }
                            Optional<TmpGamePlayerStats> statsOpt = tmpGamePlayerStatsRepository.findByPlayerId(lastPlayerId);
                            if (statsOpt.isPresent()) {
                                TmpGamePlayerStats stats = statsOpt.get();
                                stats.setTotalScore(stats.getTotalScore() - scoreToReApply);
                                tmpGamePlayerStatsRepository.save(stats);
                            }
                        }
                    }

                    // Restore score for countable throws
                    Optional<TmpGamePlayerStats> playerStats = tmpGamePlayerStatsRepository.findByPlayerId(lastThrow.getPlayer().getId());
                    if (playerStats.isPresent() && !Boolean.TRUE.equals(lastThrow.getIsNotCountable())) {
                        TmpGamePlayerStats stats = playerStats.get();
                        stats.setTotalScore(stats.getTotalScore() + calculatePoints(lastThrow.getScore(), lastThrow.getMultiplier()));
                        tmpGamePlayerStatsRepository.save(stats);
                    }

                    dartThrowRepository.save(lastThrow);
                    return game;
                } else {
                    return null;
                }

            } else {
                List<DartThrow> gameThrowsActive = dartThrowRepository.findByGameAndIsUndoFalseForUpdate(game);
                gameThrowsActive.sort(Comparator.comparing(DartThrow::getId));

                DartThrow dartThrow = DartThrow.builder()
                        .player(getCurrentPlayer(game.getPlayers(), gameThrowsActive, getRoundSize(game.getGameType())))
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

                switch (game.getGameType()) {
                    case CLASSIC:
                        // check in type: every player must hit the required multiplier before their score counts
                        // Wenn inType null ist, wird diese Prüfung übersprungen
                        if (game.getGameTypeClassicInType() != null) {
                            boolean playerHasOpened = gameThrowsActive.stream()
                                    .anyMatch(t -> t.getPlayer().getId().equals(dartThrow.getPlayer().getId())
                                            && !Boolean.TRUE.equals(t.getIsNotCountable()));
                            if (!playerHasOpened) {
                                if (dartThrow.getMultiplier() == null
                                        || !dartThrow.getMultiplier().equals(game.getGameTypeClassicInType())) {
                                    dartThrow.setIsNotCountable(true);
                                }
                            }
                        }

                        // check score
                        Long newScore = playerStats.getTotalScore() - calculatePoints(dartThrow.getScore(), dartThrow.getMultiplier());

                        if (newScore < 0) {
                            dartThrow.setIsNotCountable(true);
                            // bust: void all previous throws in this round for this player and restore their score
                            int bustRound = dartThrow.getRound();
                            Long bustPlayerId = dartThrow.getPlayer().getId();
                            long pointsToRestore = 0;
                            for (DartThrow t : gameThrowsActive) {
                                if (t.getRound() == bustRound
                                        && t.getPlayer().getId().equals(bustPlayerId)
                                        && !Boolean.TRUE.equals(t.getIsNotCountable())) {
                                    t.setIsNotCountable(true);
                                    pointsToRestore += calculatePoints(t.getScore(), t.getMultiplier());
                                    // managed entity — dirty checking persists the flag at commit
                                }
                            }
                            playerStats.setTotalScore(playerStats.getTotalScore() + pointsToRestore);
                        } else if (newScore.equals(0L)) {
                            // check out type: nur wenn outType gesetzt ist
                            // Wenn outType null ist, ist jeder Multiplier erlaubt für den Finish
                            if (game.getGameTypeClassicOutType() != null
                                    && (dartThrow.getMultiplier() == null
                                        || !dartThrow.getMultiplier().equals(game.getGameTypeClassicOutType()))) {
                                dartThrow.setIsNotCountable(true);
                            }
                        }

                        // minimum remaining score check: prevent reaching an unreachable finish.
                        // Nur wenn outType gesetzt ist
                        // - Wenn outType NULL: Keine Beschränkung, Score kann bis 1 gehen (dann Single 1 zum Ausmachen)
                        // - Wenn outType DOUBLE: Score muss >= 2 bleiben (Double 1 existiert nicht)
                        // - Wenn outType TRIPLE: Score muss >= 3 bleiben (Triple 1/2 existiert nicht)
                        if (newScore > 0 && game.getGameTypeClassicOutType() != null) {
                            boolean isBustRisk = false;
                            if (game.getGameTypeClassicOutType().equals(DartThrowMultiplierEnum.DOUBLE) && newScore < 2) {
                                isBustRisk = true;
                            } else if (game.getGameTypeClassicOutType().equals(DartThrowMultiplierEnum.TRIPLE) && newScore < 3) {
                                isBustRisk = true;
                            }
                            
                            if (isBustRisk) {
                                dartThrow.setIsNotCountable(true);
                            }
                        }

                        // winner: Spieler gewinnt wenn Score == 0 und nicht busted
                        // Wenn outType null: Score muss 0 sein (egal welcher Multiplier)
                        // Wenn outType nicht null: Score muss 0 sein UND mit dem erforderlichen Multiplier
                        if (!Boolean.TRUE.equals(dartThrow.getIsNotCountable()) && newScore.equals(0L)) {
                            boolean isValidFinish = false;
                            
                            if (game.getGameTypeClassicOutType() == null) {
                                // Wenn kein outType definiert: egal welcher Multiplier - ist ein gültiger Finish
                                isValidFinish = true;
                            } else if (dartThrow.getMultiplier() != null && game.getGameTypeClassicOutType().equals(dartThrow.getMultiplier())) {
                                // Wenn outType definiert: muss der Multiplier matchen
                                isValidFinish = true;
                            }
                            
                            if (isValidFinish) {
                                game.setEndTime(Instant.now());
                                dartThrow.getPlayer().setIsWinner(true);
                            }
                        }

                        if (!Boolean.TRUE.equals(dartThrow.getIsNotCountable())) {
                            playerStats.setTotalScore(newScore);
                        }
                        break;
                }

                if (game.getEndTime() != null) {
                    dartGameRepository.save(game);
                }

                tmpGamePlayerStatsRepository.save(playerStats);
                dartThrowRepository.save(dartThrow);
                return game;
            }

        } else {
            throw new IllegalArgumentException("Game has already ended or invalid request");
        }
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

        // work on a sorted copy so the caller's list is not mutated
        List<DartThrow> sorted = new ArrayList<>(gameThrowsActive);
        sorted.sort(Comparator.comparing(DartThrow::getId).reversed()); // newest first

        // get last active player
        Long lastActivePlayerId = null;
        for (int i = players.size() - 1; i >= 0; i--) {
            DartPlayer p = players.get(i);
            if (p.getLeftGameAt() == null) { lastActivePlayerId = p.getId(); break; }
        }
        if (lastActivePlayerId == null) return 0;

        DartThrow latest = sorted.get(0);
        int roundSize = getRoundSize(gameType);
        int currentRound = latest.getRound();
        Long currentPlayerId = latest.getPlayer().getId();

        // count how many throws the current player has in their latest round.
        // Mirror getCurrentPlayer logic: a not-countable throw (bust) ends the round early.
        int throwCount = 0;
        boolean roundEndedEarly = false;
        for (DartThrow t : sorted) {
            if (t.getRound() != currentRound) break;
            if (!t.getPlayer().getId().equals(currentPlayerId)) break;
            if (Boolean.TRUE.equals(t.getIsNotCountable())) {
                roundEndedEarly = true;
                break;
            }
            throwCount++;
            if (throwCount == roundSize) break;
        }

        // if the last active player has completed (or busted out of) their round, advance
        if ((throwCount == roundSize || roundEndedEarly) && currentPlayerId.equals(lastActivePlayerId)) {
            return currentRound + 1;
        }
        return currentRound;
    }

    private DartPlayer getCurrentPlayer(List<DartPlayer> players,
                                        List<DartThrow> gameThrowsActive,
                                        Integer roundSize) {

        if (gameThrowsActive.isEmpty()) {
            for (DartPlayer player : players) {
                if (player.getLeftGameAt() == null) return player;
            }
            throw new IllegalArgumentException("No active player found in this game");
        }

        // work on a sorted copy (newest first) so the caller's list is not mutated
        List<DartThrow> sorted = new ArrayList<>(gameThrowsActive);
        sorted.sort(Comparator.comparing(DartThrow::getId).reversed());

        Long lastPlayerId = null;
        int throwCount = 0;

        for (DartThrow dartThrow : sorted) {
            if (lastPlayerId == null) {
                lastPlayerId = dartThrow.getPlayer().getId();
            }

            // stop as soon as we hit a throw from a different player
            if (!lastPlayerId.equals(dartThrow.getPlayer().getId())) {
                break;
            }

            // a not-countable throw (bust / bad in/out) ends the round immediately for this player
            if (Boolean.TRUE.equals(dartThrow.getIsNotCountable())) {
                throwCount = roundSize;
                break;
            }

            throwCount++;
            if (throwCount >= roundSize) break;
        }

        if (throwCount < roundSize) {
            // same player still has darts left
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

            // if last player, wrap around to first active player
            for (DartPlayer player : players) {
                if (player.getLeftGameAt() == null) return player;
            }
        }

        throw new IllegalArgumentException("No active player found in this game");
    }


    /**
     * Best single-round score across all of the player's rounds.
     * Only countable throws are summed; bust/voided rounds contribute 0.
     */
    private Long computeHighscore(List<DartThrow> playerThrows) {
        if (playerThrows.isEmpty()) return 0L;
        Map<Integer, Long> scorePerRound = new HashMap<>();
        for (DartThrow t : playerThrows) {
            if (!Boolean.TRUE.equals(t.getIsNotCountable())) {
                scorePerRound.merge(t.getRound(), (long) calculatePoints(t.getScore(), t.getMultiplier()), Long::sum);
            }
        }
        return scorePerRound.values().stream().mapToLong(Long::longValue).max().orElse(0L);
    }

    /**
     * Three-dart average: total countable points scored divided by number of rounds visited.
     * Bust rounds count as a visit but contribute 0 points, matching standard darts convention.
     */
    private Double computeAverage(List<DartThrow> playerThrows) {
        if (playerThrows.isEmpty()) return 0.0;
        long totalScored = 0L;
        for (DartThrow t : playerThrows) {
            if (!Boolean.TRUE.equals(t.getIsNotCountable())) {
                totalScored += calculatePoints(t.getScore(), t.getMultiplier());
            }
        }
        long roundsVisited = playerThrows.stream().mapToInt(DartThrow::getRound).distinct().count();
        return roundsVisited > 0 ? (double) totalScored / roundsVisited : 0.0;
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
