package com.jpromi.darts.backend.models;

import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.GameTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class NewGameRequest {
    private UUID groupUuid = null;
    private UUID locationUuid = null;
    private List<NewGamePlayerRequest> players = null;
    private GameTypeEnum gameType = GameTypeEnum.CLASSIC;
    private Long gameTypeClassicPoints = null;
    private DartThrowMultiplierEnum gameTypeClassicInType = null;
    private DartThrowMultiplierEnum gameTypeClassicOutType = null;
}
