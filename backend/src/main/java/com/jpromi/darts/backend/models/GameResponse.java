package com.jpromi.darts.backend.models;

import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.GameTypeEnum;
import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.ThrowType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GameResponse {
    private UUID uuid;

    @Builder.Default
    private UUID groupUuid = null;
    // private Object location = null;
    @Builder.Default
    private List<GamePlayerResponse> players = new ArrayList<>();
    @Builder.Default
    private GameTypeEnum gameType = GameTypeEnum.CLASSIC;
    @Builder.Default
    private Long gameTypeClassicPoints = null;
    @Builder.Default
    private DartThrowMultiplierEnum gameTypeClassicInType = null;
    @Builder.Default
    private DartThrowMultiplierEnum gameTypeClassicOutType = null;
    @Builder.Default
    private Integer round = 0;
    @Builder.Default
    private Instant startTime = null;
    @Builder.Default
    private Instant endTime = null;
    @Builder.Default
    private Boolean isCancelled = false;

    @Data
    @Builder
    public static class GamePlayerResponse {
        private UUID playerUuid;
        private String name;
        private String avatar;
        private Long score;
        private Double average;
        private Long highscore;
        private Integer orderIndex;

        @Builder.Default
        private Boolean isCurrentPlayer = false;
        @Builder.Default
        private Boolean isWinner = false;
        @Builder.Default
        private Boolean isEliminated = false;

        @Builder.Default
        private List<GameThrowResponse> throwList = new ArrayList<>();

        @Builder.Default
        private List<GameThrowResponse> hints = new ArrayList<>();

        @Data
        @Builder
        public static class GameThrowResponse {
            private Integer score;
            private DartThrowMultiplierEnum multiplier;
            private Integer round;
            private ThrowType type;
            private Instant timestamp;
        }
    }
}
