package com.jpromi.darts.backend.entities;

import com.jpromi.darts.backend.enums.GameType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private GameType gameType;

    @Column(nullable = true)
    private Long startingPoints; // Only for X01

    @Column(nullable = false)
    private LocalDateTime gameStarted;

    @Column(nullable = false)
    private LocalDateTime gameEnded;

}
