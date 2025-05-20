package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
    Account findByUuid(UUID uuid);
    Account findByUuidAndIsDisabledFalseAndIsDeletedFalseAndIsEmailVerifiedTrue(UUID uuid);
    Optional<Account> findByUsernameAndIsDisabledFalseAndIsDeletedFalseAndIsEmailVerifiedTrue(String username);
    Optional<Account> findByEmailVerificationTokenAndIsEmailVerifiedFalseAndIsDeletedFalseAndIsDisabledFalse(String emailVerificationToken);
    Optional<Account> findById(Long id);
}
