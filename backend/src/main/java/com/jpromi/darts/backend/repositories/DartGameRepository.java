package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.DartGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query("""
            select distinct g
            from DartGame g
            join fetch g.players p
            left join fetch p.account a
            left join fetch a.avatar
            where g.endTime is null
              and (g.isCancelled = false or g.isCancelled is null)
              and p.account.id = :accountId
              and p.leftGameAt is null
            order by g.startTime desc
            """)
    List<DartGame> findActiveGamesByAccountId(@Param("accountId") Long accountId);

    @Query("""
            select distinct g
            from DartGame g
            join fetch g.players p
            left join fetch p.account a
            left join fetch a.avatar
            where g.endTime is null
              and (g.isCancelled = false or g.isCancelled is null)
              and g.location.id = :locationId
            order by g.startTime desc
            """)
    List<DartGame> findActiveGamesByLocationId(@Param("locationId") Long locationId);

    @Query("""
            select g
            from DartGame g
            where g.endTime is null
              and (g.isCancelled = false or g.isCancelled is null)
              and g.location.id = :locationId
            order by g.startTime desc
            """)
    List<DartGame> findActiveGameIdsByLocationId(@Param("locationId") Long locationId);
}
