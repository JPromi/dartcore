package com.jpromi.darts.backend.entities;

import com.jpromi.darts.backend.enums.ThrowMultiplier;
import com.jpromi.darts.backend.enums.ThrowType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class DartThrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private DartPlayer player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private DartGame game;

    @Column(nullable = true)
    private ThrowMultiplier multiplier;

    @Column(nullable = false)
    private ThrowType type;

    @Column(nullable = true)
    private Integer score;

    @Column(nullable = false)
    private Integer round;

    @Column(nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean isUndo = false;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
