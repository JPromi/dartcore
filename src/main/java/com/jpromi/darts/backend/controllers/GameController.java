package com.jpromi.darts.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartThrow;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import com.jpromi.darts.backend.mapper.GameResponseMapper;
import com.jpromi.darts.backend.models.GameResponse;
import com.jpromi.darts.backend.models.GameThrowRequest;
import com.jpromi.darts.backend.models.GroupInvitationResponse;
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
    private GameResponseMapper gameResponseMapper;

    @Autowired
    private GameLockRegistry gameLockRegistry;

    @PostMapping("")
    public ResponseEntity<UUID> newGame(@CookieValue("b2h.darts.session") String sessionCookie, @RequestBody NewGameRequest gameRequest) {
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
    public ResponseEntity<GameResponse> getGame(@CookieValue("b2h.darts.session") String sessionCookie, @PathVariable UUID gameUuid) {
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

        // logic
        ReentrantLock lock = gameLockRegistry.get(gameUuid);
        lock.lock();
        DartThrow dartThrow = null;
        try {
            dartThrow = gameService.addThrow(gameUuid, body);
        } finally {
            lock.unlock();
            gameLockRegistry.cleanup(gameUuid, lock);
            if (dartThrow != null) {
                sendThrowUpdate(dartThrow, gameUuid);
            }
        }
    }

    private Void sendThrowUpdate(DartThrow dartThrow, UUID gameUuid) {

        messaging.convertAndSend("/response/game/" + gameUuid + "/player",
                gameService.getPlayerResponse(dartThrow.getPlayer().getId())
        );

        return null;
    }

}
