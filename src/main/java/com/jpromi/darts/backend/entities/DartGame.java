package com.jpromi.darts.backend.entities;

import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.GameTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class DartGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false)
    private GameTypeEnum gameType;

    @OneToMany
    private List<DartPlayer> players;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = true)
    private Location location;

    @ManyToOne
    @JoinColumn(nullable = true)
    private AccountGroup group;

    @Column(nullable = true)
    private Long gameTypeClassicPoints;

    @Column(nullable = true)
    @Builder.Default
    private DartThrowMultiplierEnum gameTypeClassicInType = null;

    @Column(nullable = true)
    @Builder.Default
    private DartThrowMultiplierEnum gameTypeClassicOutType = DartThrowMultiplierEnum.DOUBLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Account creator;

    @Builder.Default
    private LocalDateTime startTime = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime endTime = null;

    @Builder.Default
    private Boolean isCancelled = false;
}
