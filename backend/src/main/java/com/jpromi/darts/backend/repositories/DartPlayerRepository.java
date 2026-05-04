package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartPlayer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DartPlayerRepository extends JpaRepository<DartPlayer, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<DartPlayer> findByGameAndLeftGameAtNullOrderByOrderIndex(DartGame game);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DartPlayer> findByGameOrderByOrderIndex(DartGame game);

    @Query("""
        select p from DartPlayer p
        left join fetch p.game
        left join fetch p.throwsList
        where p.id = :id
    """)
    Optional<DartPlayer> findByIdWithRelations(@Param("id") Long id);
}
