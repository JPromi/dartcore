package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartPlayer;
import com.jpromi.darts.backend.entities.Location;
import com.jpromi.darts.backend.entities.LocationClient;
import com.jpromi.darts.backend.entities.LocationScreen;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.models.ExternalInputContextResponse;
import com.jpromi.darts.backend.models.GameResponse;
import com.jpromi.darts.backend.models.GameThrowRequest;
import com.jpromi.darts.backend.models.NewGameRequest;
import com.jpromi.darts.backend.models.NewGameLocationResponse;
import com.jpromi.darts.backend.registry.GameLockRegistry;
import com.jpromi.darts.backend.repositories.LocationClientRepository;
import com.jpromi.darts.backend.repositories.LocationScreenRepository;
import com.jpromi.darts.backend.services.AuthService;
import com.jpromi.darts.backend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@RestController("GameController")
@RequestMapping("/api/game")
public class GameController {
    /*
    Features:
    GET - game // get game
    POST - game // new game
    POST - game/throw // add throw
    DELETE - game // close game
    */

    @Autowired
    private AuthService authService;

    @Autowired
    private GameService gameService;

    @Autowired
    private GameLockRegistry gameLockRegistry;

    @Autowired
    private LocationScreenRepository locationScreenRepository;

    @Autowired
    private LocationClientRepository locationClientRepository;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Invalid request"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Location already has an active game"));
    }

    @GetMapping("/active")
    public ResponseEntity<List<GameResponse>> getActiveGamesResponseByAccount(
            @CookieValue(name = "dcn.session", required = false) String sessionCookie,
            @RequestHeader(value = "X-Screen-Token", required = false) String screenToken,
            @RequestHeader(value = "X-Client-Token", required = false) String clientToken) {
        if (screenToken != null) {
            LocationScreen screen = locationScreenRepository.findByTokenWithLocation(screenToken).orElse(null);
            if (screen != null && !Boolean.TRUE.equals(screen.getIsTmp())) {
                return ResponseEntity.ok(gameService.getActiveGamesResponseByLocation(screen.getLocation()));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (clientToken != null) {
            LocationClient client = locationClientRepository.findByTokenWithLocation(clientToken).orElse(null);
            if (client != null && !Boolean.TRUE.equals(client.getIsTmp())) {
                return ResponseEntity.ok(gameService.getActiveGamesResponseByLocation(client.getLocation()));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if (sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);
            if (session != null) {
                return ResponseEntity.ok(gameService.getActiveGamesResponseByAccount(session.getAccount()));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/client/context")
    public ResponseEntity<ExternalInputContextResponse> getExternalInputContext(
            @RequestHeader(value = "X-Client-Token", required = false) String clientToken) {
        if (clientToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        LocationClient client = locationClientRepository.findByTokenWithLocation(clientToken).orElse(null);
        if (client == null || Boolean.TRUE.equals(client.getIsTmp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<GameResponse> activeGames = gameService.getActiveGamesResponseByLocation(client.getLocation());
        UUID activeGameUuid = activeGames.isEmpty() ? null : activeGames.getFirst().getUuid();
        return ResponseEntity.ok(ExternalInputContextResponse.builder()
                .groupUuid(client.getLocation().getGroup().getUuid())
                .locationUuid(client.getLocation().getUuid())
                .activeGameUuid(activeGameUuid)
                .build());
    }

    @GetMapping("/location/last")
    public ResponseEntity<GameResponse> getLastGameResponseByLocation(
            @RequestHeader(value = "X-Screen-Token", required = false) String screenToken) {
        if (screenToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        LocationScreen screen = locationScreenRepository.findByTokenWithLocation(screenToken).orElse(null);
        if (screen == null || Boolean.TRUE.equals(screen.getIsTmp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        GameResponse game = gameService.getLastGameResponseByLocation(screen.getLocation());
        return game != null ? ResponseEntity.ok(game) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping("")
    public ResponseEntity<UUID> newGame(@CookieValue("dcn.session") String sessionCookie, @RequestBody NewGameRequest gameRequest) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                DartGame game = gameService.newGame(gameRequest, session.getAccount());
                sendLocationGameCreated(game);
                return ResponseEntity.ok(game.getUuid());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/client")
    public ResponseEntity<UUID> newGameForClient(
            @RequestHeader(value = "X-Client-Token", required = false) String clientToken,
            @RequestBody NewGameRequest gameRequest) {
        if (clientToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        LocationClient client = locationClientRepository.findByTokenWithLocation(clientToken).orElse(null);
        if (client == null || Boolean.TRUE.equals(client.getIsTmp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        DartGame game = gameService.newGameForLocation(gameRequest, client.getLocation());
        sendLocationGameCreated(game);
        return ResponseEntity.ok(game.getUuid());
    }

    @GetMapping("/locations")
    public ResponseEntity<List<NewGameLocationResponse>> getLocationsForNewGame(
            @CookieValue("dcn.session") String sessionCookie,
            @RequestParam UUID groupUuid) {
        Session session = this.authService.session(sessionCookie);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(gameService.getLocationsForNewGame(groupUuid, session.getAccount()));
    }

    @GetMapping("/{gameUuid}")
    public ResponseEntity<GameResponse> getGame(
            @CookieValue(name = "dcn.session", required = false) String sessionCookie,
            @RequestHeader(name = "X-Screen-Token", required = false) String screenToken,
            @RequestHeader(name = "X-Client-Token", required = false) String clientToken,
            @PathVariable UUID gameUuid) {
        if (screenToken != null) {
            LocationScreen screen = locationScreenRepository.findByTokenWithLocation(screenToken).orElse(null);
            if (screen == null || Boolean.TRUE.equals(screen.getIsTmp())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            DartGame dartGame = this.gameService.getGameByUuid(gameUuid);
            if (dartGame == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            if (dartGame.getLocation() == null || !dartGame.getLocation().getId().equals(screen.getLocation().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            return ResponseEntity.ok(this.gameService.getGameResponseByUuid(dartGame));
        }
        if (clientToken != null) {
            LocationClient client = locationClientRepository.findByTokenWithLocation(clientToken).orElse(null);
            if (client == null || Boolean.TRUE.equals(client.getIsTmp())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            DartGame dartGame = this.gameService.getGameByUuid(gameUuid);
            if (dartGame == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            if (dartGame.getLocation() == null || !dartGame.getLocation().getId().equals(client.getLocation().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            return ResponseEntity.ok(this.gameService.getGameResponseByUuid(dartGame));
        }
        if (sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);
            if (session != null) {
                GameResponse game = this.gameService.getGameResponseByUuid(gameUuid);
                return game != null ? ResponseEntity.ok(game) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    
    @DeleteMapping("/{gameUuid}")
    public ResponseEntity<Void> endGame(@CookieValue("dcn.session") String sessionCookie, @PathVariable UUID gameUuid) {
        if (sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if (session != null) {
                ReentrantLock lock = gameLockRegistry.get(gameUuid);
                lock.lock();
                boolean success = false;
                try {
                    success = this.gameService.endGame(gameUuid) != null;
                } finally {
                    lock.unlock();
                    gameLockRegistry.cleanup(gameUuid, lock);
                    if (success) {
                        sendGameUpdate(gameUuid);
                    }
                }

                if (success) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // WS
    private final SimpMessagingTemplate messaging;
    public GameController(SimpMessagingTemplate messaging){ this.messaging = messaging; }


    @MessageMapping("/game/{gameUuid}/throw")
    public void addThrow(
            @DestinationVariable UUID gameUuid,
            @Header("simpSessionAttributes") Map<String, Object> attrs,
            @Payload GameThrowRequest body
    ) {
        String sessionCookie = (String) attrs.get("sessionCookie");
        String clientToken = (String) attrs.get("clientToken");
        Session session = sessionCookie != null ? authService.session(sessionCookie) : null;
        System.out.println("GameController.addThrow: " + gameUuid + " - " + body);

        // authorization: ensure the session account is a player in this game and the game is active
        DartGame game = gameService.getGameByUuid(gameUuid);
        if (game == null) {
            messaging.convertAndSend("/response/game/" + gameUuid + "/error", Map.of("message", "Game not found"));
            return;
        }

        if (game.getEndTime() != null) {
            messaging.convertAndSend("/response/game/" + gameUuid + "/error", Map.of("message", "Game already ended"));
            return;
        }

        if (clientToken != null) {
            LocationClient client = locationClientRepository.findByTokenWithLocation(clientToken).orElse(null);
            if (client == null || Boolean.TRUE.equals(client.getIsTmp())
                    || game.getLocation() == null
                    || !game.getLocation().getId().equals(client.getLocation().getId())) {
                messaging.convertAndSend("/response/game/" + gameUuid + "/error", Map.of("message", "Client not authorized for this game"));
                return;
            }
        } else {
            if (session == null) throw new IllegalArgumentException("Session invalid");
            boolean isPlayerInGame = false;
            for (DartPlayer p : game.getPlayers()) {
                if (p.getAccount() != null && session.getAccount() != null
                        && p.getAccount().getId() != null && session.getAccount().getId() != null
                        && p.getAccount().getId().equals(session.getAccount().getId())
                        && p.getLeftGameAt() == null) {
                    isPlayerInGame = true;
                    break;
                }
            }

            if (!isPlayerInGame) {
                messaging.convertAndSend("/response/game/" + gameUuid + "/error", Map.of("message", "Not authorized to play in this game"));
                return;
            }
        }

        // logic
        ReentrantLock lock = gameLockRegistry.get(gameUuid);
        lock.lock();
        boolean success = false;
        try {
            success = gameService.addThrow(gameUuid, body) != null;
        } finally {
            lock.unlock();
            gameLockRegistry.cleanup(gameUuid, lock);
            if (success) {
                sendGameUpdate(gameUuid);
            }
        }
    }

    private void sendGameUpdate(UUID gameUuid) {
        GameResponse gameDto = gameService.getGameResponseByUuid(gameUuid);
        messaging.convertAndSend("/response/game/" + gameUuid, gameDto);
    }

    private void sendLocationGameCreated(DartGame game) {
        Location location = game.getLocation();
        if (location == null) {
            return;
        }

        GameResponse gameDto = gameService.getGameResponseByUuid(game);
        for (LocationScreen screen : locationScreenRepository.findByLocation(location)) {
            if (!Boolean.TRUE.equals(screen.getIsTmp())) {
                messaging.convertAndSend("/response/location-screen/" + screen.getToken() + "/game-created", gameDto);
            }
        }
    }

}
