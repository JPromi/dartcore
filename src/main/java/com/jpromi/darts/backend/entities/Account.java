package com.jpromi.darts.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.List;
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
    @Builder.Default
    private Boolean isTotpEnabled = false;

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
    private File avatar;

    @Column(nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean isDisabled = false;

    @Column(nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean isEmailVerified = false;

    @Column(nullable = true, unique = true)
    private String emailVerificationToken;

    @Column(nullable = true)
    private LocalDateTime emailVerificationTimestamp;

    @Column(nullable = true)
    private String passwordResetToken;

    @Column(nullable = true)
    private LocalDateTime passwordResetTokenTimestamp;

    @Column(nullable = true)
    private LocalDateTime registrationTimestamp;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountGroupMember> groupMemberships;

//    @Transient
//    public List<AccountGroup> getGroups() {
//        return groupMemberships.stream().map(AccountGroupMember::getAccountGroup).toList();
//    }

}
