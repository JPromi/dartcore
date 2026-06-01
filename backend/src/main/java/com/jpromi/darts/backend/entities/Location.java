package com.jpromi.darts.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountGroup group;

    @Column(nullable = true)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean isPublic = false;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String address;

}
