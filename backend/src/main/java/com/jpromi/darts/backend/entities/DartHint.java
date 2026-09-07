package com.jpromi.darts.backend.entities;

import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.GameTypeEnum;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DartHint {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameTypeEnum gameType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private DartThrowMultiplierEnum gameTypeClassicOutType;

    @Column(nullable = false)
    private Long points;

    @Column(nullable = true)
    private Integer t1Points;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private DartThrowMultiplierEnum t1Multiplier;

    @Column(nullable = true)
    private Integer t2Points;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private DartThrowMultiplierEnum t2Multiplier;

    @Column(nullable = true)
    private Integer t3Points;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private DartThrowMultiplierEnum t3Multiplier;

}
