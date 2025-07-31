package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import com.jpromi.darts.backend.models.GroupInvitationResponse;
import com.jpromi.darts.backend.models.NewGameRequest;
import com.jpromi.darts.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("")
    public ResponseEntity<String> newGame(@CookieValue("b2h.darts.session") String sessionCookie, @RequestBody NewGameRequest gameRequest) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                // Typically code
                return null;
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
