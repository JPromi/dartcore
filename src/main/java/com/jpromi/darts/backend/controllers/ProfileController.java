package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.Profile;
import com.jpromi.darts.backend.models.ProfileResponse;
import com.jpromi.darts.backend.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("ProfileController")
@RequestMapping("/api/profile/")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String username) {
        ProfileResponse profile = profileService.getProfile(username);
        return ResponseEntity.ok(profile);
    }
}
