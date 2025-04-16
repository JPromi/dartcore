package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.models.GroupResponse;
import com.jpromi.darts.backend.models.SessionAccountResponse;
import com.jpromi.darts.backend.services.AuthService;
import com.jpromi.darts.backend.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("GroupController")
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private AuthService authService;

    @GetMapping("")
    public ResponseEntity<List<GroupResponse>> getGroup(@CookieValue("b2h.darts.session") String sessionCookie) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                List<GroupResponse> groups = this.groupService.getGroupsByAccount(session.getAccount());
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

}
