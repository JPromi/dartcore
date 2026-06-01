package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LocationRequest {
    private String name;
    private String description;
    private Boolean isPublic;
    private String address;
}
