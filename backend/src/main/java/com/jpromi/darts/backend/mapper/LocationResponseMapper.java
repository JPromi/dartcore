package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.Location;
import com.jpromi.darts.backend.models.LocationResponse;
import org.springframework.stereotype.Component;

@Component
public class LocationResponseMapper {

    public LocationResponse fromLocation(Location location) {
        return LocationResponse.builder()
                .uuid(location.getUuid())
                .name(location.getName())
                .description(location.getDescription())
                .isPublic(location.getIsPublic())
                .address(location.getAddress())
                .screens(null)
                .inputClients(null)
                .build();
    }
}
