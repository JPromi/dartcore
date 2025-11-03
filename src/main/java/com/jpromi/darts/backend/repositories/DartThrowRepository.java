package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartThrow;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DartThrowRepository extends JpaRepository<DartThrow, Long> {
    Boolean existsByGameAndIsUndoFalse(DartGame game);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from DartThrow t where t.game = :game and t.isUndo = false")
    List<DartThrow> findByGameAndIsUndoFalseForUpdate(@Param("game") DartGame game);

    List<DartThrow> findByGameAndIsUndoFalse(DartGame game);
}
