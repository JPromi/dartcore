package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.DartThrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DartThrowRepository extends JpaRepository<DartThrow, Long> {
    List<DartThrow> findByGameAndIsUndoFalse(DartGame game);
}
