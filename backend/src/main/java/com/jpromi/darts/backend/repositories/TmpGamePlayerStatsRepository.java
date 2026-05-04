package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.TmpGamePlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TmpGamePlayerStatsRepository extends JpaRepository<TmpGamePlayerStats, Long> {
    Optional<TmpGamePlayerStats> findByPlayerId(Long playerId);
    TmpGamePlayerStats findByGameId(Long gameId);
}
