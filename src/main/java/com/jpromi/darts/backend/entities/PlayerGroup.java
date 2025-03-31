package com.jpromi.darts.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class PlayerGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private Boolean isPrivate = true;

    @Column(nullable = false)
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Account> members;

    @Column(nullable = false)
    @OneToMany(cascade = CascadeType.ALL)
    private List<Location> locations;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted;

}
