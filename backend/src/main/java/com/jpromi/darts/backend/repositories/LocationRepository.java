package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByUuid(UUID uuid);
    Optional<Location> findByUuidAndGroup(UUID uuid, AccountGroup group);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Location l where l.uuid = :uuid and l.group = :group")
    Optional<Location> findByUuidAndGroupForUpdate(@Param("uuid") UUID uuid, @Param("group") AccountGroup group);
    List<Location> findByGroup(AccountGroup group);
    Optional<Location> findByUuidAndIsPublicIsTrue(UUID uuid);

//    @Query("""
//    SELECT l
//    FROM Location l
//    LEFT JOIN l.groups g
//    WHERE g = :group
//    """)
//    Location findByGroup(AccountGroup group);
}
