package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroupInvitationAccount;
import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountGroupInvitationAccountRepository extends JpaRepository<AccountGroupInvitationAccount, Long> {
    List<AccountGroupInvitationAccount> findByAccount(Account account);
    List<AccountGroupInvitationAccount> findByAccountAndStatus(Account account, InvitationStatusAccountEnum status);
    AccountGroupInvitationAccount findByUuidAndAccount(UUID uuid, Account account);
    Long countByAccountAndStatus(Account account, InvitationStatusAccountEnum status);
}
