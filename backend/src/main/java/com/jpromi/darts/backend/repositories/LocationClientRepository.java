package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.Location;
import com.jpromi.darts.backend.entities.LocationClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationClientRepository extends JpaRepository<LocationClient, Long> {
	List<LocationClient> findByLocation(Location location);

	@Query("select lc from LocationClient lc join fetch lc.location l join fetch l.group where lc.token = :token")
	Optional<LocationClient> findByTokenWithLocation(@Param("token") String token);
}
