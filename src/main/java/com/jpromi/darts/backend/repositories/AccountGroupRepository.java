package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountGroupRepository extends JpaRepository<AccountGroup, Long> {
    AccountGroup findByUuid(UUID uuid);
}
