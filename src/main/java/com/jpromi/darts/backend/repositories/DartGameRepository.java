package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.DartGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DartGameRepository extends JpaRepository<DartGame, Long> {
    Optional<DartGame> findByUuid(UUID uuid);

    @Query("""
            select distinct g
            from DartGame g
            left join fetch g.players p
            left join fetch p.account a
            left join fetch a.avatar
            where g.uuid = :uuid
            """)
    Optional<DartGame> findByUuidWithPlayersAndAccounts(@Param("uuid") UUID uuid);
}
