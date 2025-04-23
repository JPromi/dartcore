package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.models.SettingAccountRequest;
import com.jpromi.darts.backend.models.SettingAccountResponse;
import com.jpromi.darts.backend.models.SettingProfileResponse;
import com.jpromi.darts.backend.services.AuthService;
import com.jpromi.darts.backend.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("SettingController")
@RequestMapping("/api/setting")
public class SettingController {

    @Autowired
    private SettingService settingService;

    @Autowired
    private AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<SettingProfileResponse> getProfile(@CookieValue("b2h.darts.session") String sessionCookie) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if (session != null)
            {
                SettingProfileResponse profile = this.settingService.getProfile(session.getAccount());
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


    @PutMapping("/profile")
    public ResponseEntity<SettingProfileResponse> updateProfile(@CookieValue("b2h.darts.session") String sessionCookie, @RequestBody SettingProfileResponse request) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if (session != null)
            {
                SettingProfileResponse profile = this.settingService.updateProfile(session.getAccount(), request);
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/account")
    public ResponseEntity<SettingAccountResponse> getAccount(@CookieValue("b2h.darts.session") String sessionCookie) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if (session != null)
            {
                SettingAccountResponse account = this.settingService.getAccount(session.getAccount());
                return ResponseEntity.ok(account);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping("/account")
    public ResponseEntity<SettingAccountResponse> updateAccount(@CookieValue("b2h.darts.session") String sessionCookie, @RequestBody SettingAccountRequest request) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if (session != null)
            {
                SettingAccountResponse account = this.settingService.updateAccount(session.getAccount(), request);
                return ResponseEntity.ok(account);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
