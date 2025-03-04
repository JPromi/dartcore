package com.jpromi.darts.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String locationName;

    @Column(nullable = true)
    private String boardManufacturer;

    @Column(nullable = true)
    private String boardModel;

}
