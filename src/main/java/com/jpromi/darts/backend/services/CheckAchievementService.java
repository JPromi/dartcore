package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.Achievement;
import com.jpromi.darts.backend.entities.DartThrow;

import java.util.List;

public interface CheckAchievementService {
    Boolean checkAchievement(String condition, List<DartThrow> dartThrows);
}
