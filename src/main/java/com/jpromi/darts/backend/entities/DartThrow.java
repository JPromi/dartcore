package com.jpromi.darts.backend.entities;

import com.jpromi.darts.backend.enums.ThrowMultiplier;
import com.jpromi.darts.backend.enums.ThrowType;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private Long gameId;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = true)
    private ThrowMultiplier multiplier;

    @Column(nullable = false)
    private ThrowType type = ThrowType.THROW;

    @Column(nullable = true)
    private Integer score;

    @Column(nullable = true)
    private Integer distanceMm;

    @Column(nullable = false)
    private Integer round;

    @Column(nullable = false)
    private Boolean isUndo = false;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
