package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.DartGame;
import com.jpromi.darts.backend.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByUuid(UUID uuid);

//    @Query("""
//    SELECT l
//    FROM Location l
//    LEFT JOIN l.groups g
//    WHERE g = :group
//    """)
//    Location findByGroup(AccountGroup group);
}
