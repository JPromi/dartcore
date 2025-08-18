package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import com.jpromi.darts.backend.mapper.GameResponseMapper;
import com.jpromi.darts.backend.models.GameResponse;
import com.jpromi.darts.backend.models.GroupInvitationResponse;
import com.jpromi.darts.backend.models.NewGameRequest;
import com.jpromi.darts.backend.services.AuthService;
import com.jpromi.darts.backend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
                DartGame game = this.gameService.getGameByUuid(gameUuid);
                if (game != null) {
                    return ResponseEntity.ok(gameResponseMapper.fromDartGame(game));
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
}
