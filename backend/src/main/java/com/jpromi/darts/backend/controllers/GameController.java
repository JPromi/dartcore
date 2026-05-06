package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartPlayer;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.models.GameResponse;
import com.jpromi.darts.backend.models.GameThrowRequest;
import com.jpromi.darts.backend.models.NewGameRequest;
import com.jpromi.darts.backend.registry.GameLockRegistry;
import com.jpromi.darts.backend.services.AuthService;
import com.jpromi.darts.backend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Invalid request"));
    }

    @GetMapping("/active")
    public ResponseEntity<List<GameResponse>> getActiveGamesResponseByAccount(@CookieValue("dcn.session") String sessionCookie) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                return ResponseEntity.ok(gameService.getActiveGamesResponseByAccount(session.getAccount()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("")
    public ResponseEntity<UUID> newGame(@CookieValue("dcn.session") String sessionCookie, @RequestBody NewGameRequest gameRequest) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {

                return ResponseEntity.ok(gameService.newGame(gameRequest, session.getAccount()).getUuid());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/{gameUuid}")
    public ResponseEntity<GameResponse> getGame(@CookieValue("dcn.session") String sessionCookie, @PathVariable UUID gameUuid) {
        if (sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if (session != null) {
                GameResponse game = this.gameService.getGameResponseByUuid(gameUuid);
                if (game != null) {
                    return ResponseEntity.ok(game);
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
        Session session = authService.session(sessionCookie);
        if (session == null) throw new IllegalArgumentException("Session invalid");
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

}
