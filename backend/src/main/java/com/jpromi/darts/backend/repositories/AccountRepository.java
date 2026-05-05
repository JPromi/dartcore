package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    @Query(
            value = """
WITH rel AS (
  SELECT a.id AS target_id,
         EXISTS (
           SELECT 1
           FROM account_group_member gm1
           JOIN account_group_member gm2
             ON gm1.account_group_id = gm2.account_group_id
           WHERE gm1.account_id = :viewerId
             AND gm2.account_id = a.id
         ) AS same_group
  FROM account a
)
SELECT a.*
FROM account a
JOIN rel r ON r.target_id = a.id
WHERE a.username ILIKE CONCAT('%', :q, '%')
  AND (:isPlayable IS NULL
       OR (:isPlayable = TRUE  AND r.same_group)
       OR (:isPlayable = FALSE AND NOT r.same_group))
  AND a.isDeleted = FALSE AND a.isDisabled = FALSE AND a.isEmailVerified = TRUE
ORDER BY a.username
""",
            countQuery = """
WITH rel AS (
  SELECT a.id AS target_id,
         EXISTS (
           SELECT 1
           FROM account_group_member gm1
           JOIN account_group_member gm2
             ON gm1.account_group_id = gm2.account_group_id
           WHERE gm1.account_id = :viewerId
             AND gm2.account_id = a.id
         ) AS same_group
  FROM account a
)
SELECT COUNT(*)
FROM account a
JOIN rel r ON r.target_id = a.id
WHERE a.username ILIKE CONCAT('%', :q, '%')
  AND (:isPlayable IS NULL
       OR (:isPlayable = TRUE  AND r.same_group)
       OR (:isPlayable = FALSE AND NOT r.same_group))
  AND a.isDeleted = FALSE AND a.isDisabled = FALSE AND a.isEmailVerified = TRUE
""",
            nativeQuery = true
    )
    Page<Account> searchByUsername(
            @Param("q") String query,
            @Param("viewerId") Long viewerId,
            @Param("isPlayable") Boolean isPlayable,
            Pageable pageable
    );

    @Query("""
    SELECT a
    FROM AccountGroup g
    JOIN g.members gmViewer
    JOIN g.members gm
    JOIN gm.account a
    WHERE g.uuid = :groupUuid
      AND gmViewer.account.id = :viewerId
      AND LOWER(a.username) LIKE LOWER(CONCAT('%', :q, '%'))
      AND a.isDeleted = FALSE AND a.isDisabled = FALSE AND a.isEmailVerified = TRUE
    """)
    Page<Account> searchByUsernameInGroup(
            @Param("q") String query,
            @Param("viewerId") Long viewerId,
            @Param("groupUuid") UUID groupUuid,
            Pageable pageable
    );


    @Query("""
    SELECT a
    FROM Account a
    WHERE LOWER(a.username) LIKE LOWER(CONCAT('%', :q, '%'))
      AND (:viewerId IS NULL OR a.id <> :viewerId)
      AND NOT EXISTS (
          SELECT 1
          FROM AccountGroup g
          JOIN g.members gm
          WHERE g.uuid = :groupUuid
            AND gm.account.id = a.id
      )
     AND NOT EXISTS (
         SELECT 1
         FROM AccountGroup g2
         JOIN g2.invitations i
         WHERE g2.uuid = :groupUuid
           AND i.account.id = a.id
           AND i.status <> "REJECTED"
     )
     AND a.isDeleted = FALSE AND a.isDisabled = FALSE AND a.isEmailVerified = TRUE
    """)
    Page<Account> searchByUsernameNotInGroup(
            @Param("q") String query,
            @Param("viewerId") Long viewerId,
            @Param("groupUuid") UUID groupUuid,
            Pageable pageable
    );

}
