package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.models.GroupLightResponse;
import com.jpromi.darts.backend.models.GroupResponse;
import com.jpromi.darts.backend.models.SessionAccountResponse;
import com.jpromi.darts.backend.services.AuthService;
import com.jpromi.darts.backend.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("GroupController")
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private AuthService authService;

    @GetMapping("")
    public ResponseEntity<List<GroupLightResponse>> getGroup(@CookieValue("b2h.darts.session") String sessionCookie) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                List<GroupLightResponse> groups = this.groupService.getGroupsByAccount(session.getAccount());
                if(groups != null) {
                    return ResponseEntity.ok(groups);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<GroupResponse> getGroupByUuid(@CookieValue("b2h.darts.session") String sessionCookie, @PathVariable String uuid) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                GroupResponse group = this.groupService.getGroupByUuid(UUID.fromString(uuid), session.getAccount());
                if(group != null) {
                    return ResponseEntity.ok(group);
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
