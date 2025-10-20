package com.jpromi.darts.backend.models;

import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.ThrowType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GameThrowRequest {
    private ThrowType type;
    private Integer point;
    private DartThrowMultiplierEnum multiplier;
    private Boolean isUndo;
}
