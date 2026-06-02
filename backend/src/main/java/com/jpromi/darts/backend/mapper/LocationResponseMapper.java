package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.Location;
import com.jpromi.darts.backend.entities.LocationClient;
import com.jpromi.darts.backend.entities.LocationScreen;
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
                .clients(location.getActiveClients().stream()
                    .map(this::clientFromLocationScreen)
                    .toList())
                .screens(location.getActiveScreens().stream()
                    .map(this::screenFromLocationScreen)
                    .toList())
                .build();
    }

    public LocationResponse.Screen screenFromLocationScreen(LocationScreen screen) {
        return LocationResponse.Screen.builder()
                .uuid(screen.getUuid())
                .name(screen.getName())
                .token(screen.getToken())
                .build();
    }

    public LocationResponse.Client clientFromLocationScreen(LocationClient client) {
        return LocationResponse.Client.builder()
                .uuid(client.getUuid())
                .name(client.getName())
                .token(client.getToken())
                .build();
    }
}
