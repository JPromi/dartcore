package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.models.*;
import com.jpromi.darts.backend.services.AuthService;
import com.jpromi.darts.backend.services.GroupService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/search")
    public ResponseEntity<PageResponse<GroupLightResponse>> getGroupSearch(
            @CookieValue("b2h.darts.session") String sessionCookie,
            @RequestParam(value = "q", required = false, defaultValue = "") String query,
            @RequestParam(value = "isMember", required = false, defaultValue = "") Boolean isMember,
            @RequestParam(value = "isPublic", required = false, defaultValue = "") Boolean isPublic,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "36") int size
    ) {
        size = Math.min(size, 120);

        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                PageResponse<GroupLightResponse> groups = this.groupService.searchGroups(query, session.getAccount(), Pageable.ofSize(size).withPage(page), isMember, isPublic);
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

    @PostMapping("")
    public ResponseEntity<GroupResponse> createGroup(@CookieValue("b2h.darts.session") String sessionCookie, @RequestBody GroupRequest group) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                GroupResponse createdGroup = this.groupService.createGroup(group, session.getAccount());
                if(createdGroup != null) {
                    return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
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

}
