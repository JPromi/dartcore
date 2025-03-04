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
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false)
    private String username;

    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isTotpEnabled = false;

    @Column(nullable = true)
    private String totpSecret;

    @Column(nullable = false)
    private String email;

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @ManyToOne
    private File profilePicture;

    @Column(nullable = false)
    private Boolean isDisabled = false;

    @Column(nullable = false)
    private Boolean isDeleted = false;
}
