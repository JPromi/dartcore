package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.Location;
import com.jpromi.darts.backend.entities.LocationScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationScreenRepository extends JpaRepository<LocationScreen, Long> {
    Optional<LocationScreen> findByUuidAndLocation(UUID uuid, Location location);
    List<LocationScreen> findByLocation(Location location);

    @Query("select ls from LocationScreen ls join fetch ls.location where ls.token = :token")
    Optional<LocationScreen> findByTokenWithLocation(@Param("token") String token);
}
