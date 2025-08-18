package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroupInvitationAccount;
import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DartGameRepository extends JpaRepository<DartGame, Long> {
    Optional<DartGame> findByUuid(UUID uuid);
}
