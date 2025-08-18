package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NewGamePlayerRequest {
    private UUID accountUuid;
    private String name;
}
