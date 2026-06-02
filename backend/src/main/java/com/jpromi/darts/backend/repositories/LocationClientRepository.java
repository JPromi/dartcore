package com.jpromi.darts.backend.repositories;

import com.jpromi.darts.backend.entities.Location;
import com.jpromi.darts.backend.entities.LocationClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationClientRepository extends JpaRepository<LocationClient, Long> {
	List<LocationClient> findByLocation(Location location);
}
