package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountGroupRepository extends JpaRepository<AccountGroup, Long> {
    AccountGroup findByUuid(UUID uuid);
    @Query("""
    SELECT ag
    FROM AccountGroup ag
    LEFT JOIN ag.members m
    WHERE (:query IS NULL OR LOWER(ag.name) LIKE LOWER(CONCAT('%', :query, '%')))
      AND (
        :isPublic IS NULL OR ag.isPublic = :isPublic
      )
      AND (
        :isMember IS NULL
        OR (:isMember = true AND :accountId IN (
              SELECT a.id FROM ag.members m JOIN m.account a WHERE a.id = :accountId
           ))
        OR (:isMember = false AND :accountId NOT IN (
              SELECT a.id FROM ag.members m JOIN m.account a
           ))
      )
    GROUP BY ag
    ORDER BY COUNT(m) DESC
    """)
    Page<AccountGroup> searchGroups(
            @Param("query") String query,
            @Param("accountId") Long accountId,
            @Param("isMember") Boolean isMember,
            @Param("isPublic") Boolean isPublic,
            Pageable pageable
    );
    @Query("""
    SELECT
        COUNT(DISTINCT i) + COUNT(DISTINCT m)
    FROM AccountGroup ag
    LEFT JOIN ag.members m
    LEFT JOIN ag.invitations i ON i.status = 'PENDING'
    WHERE ag.uuid = :groupUuid
    """)
    Long countActiveMembersByGroupUuid(UUID groupUuid);
}
