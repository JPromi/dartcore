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

    @Column(nullable = false)
    private Long gameId;

    @Column(nullable = true)
    private Long accountId;

    @Column(nullable = true)
    private String guestName;

    @Column(nullable = true)
    private Integer leftGameAt;
}
