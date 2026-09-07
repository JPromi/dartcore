package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.AccountGroupMember;
import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountGroupMemberRepository extends JpaRepository<AccountGroupMember, Long> {
    boolean existsByAccountAndAccountGroup(Account account, AccountGroup accountGroup);
}
