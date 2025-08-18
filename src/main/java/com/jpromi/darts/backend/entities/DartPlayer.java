package com.jpromi.darts.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class DartPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private DartGame game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Account account;

    @Column(nullable = true)
    private String guestName;

    @Column(nullable = true)
    private Integer leftGameAt;

    /* @Column(nullable = false)
    @Builder.Default
    private Long orderIndex = 99L; */
}
