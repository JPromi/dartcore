package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.Profile;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.models.ProfileLightResponse;
import com.jpromi.darts.backend.models.ProfileResponse;
import com.jpromi.darts.backend.models.SessionAccountResponse;
import com.jpromi.darts.backend.services.AuthService;
import com.jpromi.darts.backend.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("ProfileController")
@RequestMapping("/api/profile/")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AuthService authService;

    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponse> getProfile(@CookieValue("b2h.darts.session") String sessionCookie, @PathVariable String username) {

        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if (session != null) {
                ProfileResponse profile = profileService.getProfile(username, session.getAccount());

                if(profile != null) {
                    return ResponseEntity.ok(profile);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ProfileLightResponse> searchProfiles(
            @CookieValue("b2h.darts.session") String sessionCookie,
            @RequestParam(value = "q", required = false, defaultValue = "") String query,
            @RequestParam(value = "isFriend", required = false, defaultValue = "") Boolean isFriend,
            @RequestParam(value = "group", required = false, defaultValue = "") String inGroup,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "36") int size
    ) {
        return null;
    }
}
