package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.DartThrow;
import com.jpromi.darts.backend.enums.ThrowMultiplier;
import com.jpromi.darts.backend.enums.ThrowType;
import com.jpromi.darts.backend.models.LoginRequest;
import com.jpromi.darts.backend.services.CheckAchievementService;
import com.password4j.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("")
@RequestMapping("/api")
public class TestController {

    @Autowired
    private CheckAchievementService checkAchievementService;

    @GetMapping("/ach")
    public String test() {
        List<DartThrow> dartThrows = List.of(
                new DartThrow(null, 1L, 1L, ThrowMultiplier.TRIPLE, ThrowType.THROW, 20, 1500, 1, false, LocalDateTime.now()),
                new DartThrow(null, 1L, 1L, ThrowMultiplier.TRIPLE, ThrowType.THROW, 20, 1200, 1, false, LocalDateTime.now()),
                new DartThrow(null, 1L, 1L, ThrowMultiplier.TRIPLE, ThrowType.THROW, 20, 1800, 1, false, LocalDateTime.now())
        );
        String condition = "ANY:T20;ANY:T20;ANY:T20";

        return checkAchievementService.checkAchievement(condition, dartThrows).toString();
    }

    @PostMapping("/argon2")
    public String testArgon2(@RequestBody String request) {
        return Password.hash(request).withArgon2().getResult();
    }
}
