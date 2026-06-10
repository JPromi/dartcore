package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.DartHint;
import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.GameTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DartHintRepository extends JpaRepository<DartHint, Long> {

    // Classic Hint
    @Query("""
    SELECT d
    FROM DartHint d
    WHERE d.gameType = com.jpromi.darts.backend.enums.GameTypeEnum.CLASSIC
      AND d.gameTypeClassicOutType = :gameTypeClassicOutType
      AND d.points = :points
    """)
    Optional<DartHint> findClassicHint(
            @Param("gameTypeClassicOutType") DartThrowMultiplierEnum gameTypeClassicOutType,
            @Param("points") Integer points
    );
}
