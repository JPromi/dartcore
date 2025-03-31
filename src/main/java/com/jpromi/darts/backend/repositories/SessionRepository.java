package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Session findByTokenAndIsActiveTrue(String token);
}
