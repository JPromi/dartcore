package com.jpromi.darts.backend.models;

import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.GameTypeEnum;
import com.jpromi.darts.backend.enums.ThrowMultiplier;
import com.jpromi.darts.backend.enums.ThrowType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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
    private LocalDateTime startedAt = null;
    @Builder.Default
    private LocalDateTime finishedAt = null;
    @Builder.Default
    private Boolean isCancelled = false;

    @Data
    @Builder
    public static class GamePlayerResponse {
        private UUID accountUuid;
        private String name;
        private String avatarUrl;
        private Integer score;

        @Builder.Default
        private Boolean isCurrentPlayer = false;
        @Builder.Default
        private Boolean isWinner = false;

        @Builder.Default
        private List<GameThrowResponse> trows = new ArrayList<>();

        @Data
        @Builder
        public static class GameThrowResponse {
            private Integer score;
            private ThrowMultiplier multiplier;
            private Integer round;
            private ThrowType type;
            private LocalDateTime timestamp;
        }
    }
}
