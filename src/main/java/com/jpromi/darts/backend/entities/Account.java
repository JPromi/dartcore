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
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false)
    private String username;

    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isTotpEnabled;

    @Column(nullable = true)
    private String totpSecret;

    @OneToMany()
    private TotpFallback[] totpFallback;

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @ManyToOne
    private File profilePicture;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDisabled;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted;
}
