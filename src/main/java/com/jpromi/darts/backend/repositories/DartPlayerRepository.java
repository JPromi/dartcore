package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DartPlayerRepository extends JpaRepository<DartPlayer, Long> {
}
