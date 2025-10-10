package com.jpromi.darts.backend.models;

import com.jpromi.darts.backend.enums.ThrowMultiplier;
import com.jpromi.darts.backend.enums.ThrowType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GameThrowRequest {
    private UUID playerUuid;
    private ThrowType type;
    private Integer point;
    private ThrowMultiplier multiplier;
    private Integer distance;
    private Integer round;
    private Integer throwPosition;
}
