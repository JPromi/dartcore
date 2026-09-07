package com.jpromi.darts.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class TotpFallback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Boolean isUsed = false;

    @Column(nullable = true)
    private Instant usedAt;
}
