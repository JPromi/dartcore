package com.jpromi.darts.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean needsTotp;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isActive;
}
