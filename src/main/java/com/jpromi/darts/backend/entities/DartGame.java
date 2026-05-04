package com.jpromi.darts.backend.entities;

import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.GameTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class DartGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    @Builder.Default
    @ToString.Include
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false)
    private GameTypeEnum gameType;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DartPlayer> players = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DartThrow> throwsList = new ArrayList<>();

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

    @Column(nullable = true)
    @Builder.Default
    private LocalDateTime endTime = null;

    @Column(nullable = true)
    @Builder.Default
    private Boolean isCancelled = false;

    public void addPlayer(DartPlayer p) {
        players.add(p);
        p.setGame(this);
    }
    public void removePlayer(DartPlayer p) {
        players.remove(p);
        p.setGame(null);
    }
}
