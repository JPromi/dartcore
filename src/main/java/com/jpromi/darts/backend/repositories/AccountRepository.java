package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
    Optional<Account> findByUsernameAndIsDisabledFalseAndIsDeletedFalseAndIsEmailVerifiedTrue(String username);
    Optional<Account> findByEmailVerificationTokenAndIsEmailVerifiedFalseAndIsDeletedFalseAndIsDisabledFalse(String emailVerificationToken);
}
