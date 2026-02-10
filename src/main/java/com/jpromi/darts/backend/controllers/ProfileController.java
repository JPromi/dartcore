package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.Profile;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.models.PageResponse;
import com.jpromi.darts.backend.models.ProfileLightResponse;
import com.jpromi.darts.backend.models.ProfileResponse;
import com.jpromi.darts.backend.models.SessionAccountResponse;
import com.jpromi.darts.backend.repositories.AccountRepository;
import com.jpromi.darts.backend.services.AuthService;
import com.jpromi.darts.backend.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController("ProfileController")
@RequestMapping("/api/profile/")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponse> getProfile(@CookieValue("dcn.session") String sessionCookie, @PathVariable String username) {

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
    public ResponseEntity<PageResponse<ProfileLightResponse>> searchProfiles(
            @CookieValue("dcn.session") String sessionCookie,
            @RequestParam(value = "q", required = false, defaultValue = "") String query,
            // @RequestParam(value = "isFriend", required = false, defaultValue = "") Boolean isFriend,
            @RequestParam(value = "isPlayable", required = false, defaultValue = "false") Boolean isPlayable,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if (session != null) {
                PageResponse<ProfileLightResponse> profiles = profileService.searchProfile(query, session.getAccount(), isPlayable, pageable);

                return ResponseEntity.ok(profiles);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
