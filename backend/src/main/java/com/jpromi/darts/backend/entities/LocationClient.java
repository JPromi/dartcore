package com.jpromi.darts.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class LocationClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String token;

    @Column
    @Builder.Default
    private Instant generatedAt = Instant.now();

    @Column(nullable = true)
    @Builder.Default
    private Boolean isTmp = true;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

}
