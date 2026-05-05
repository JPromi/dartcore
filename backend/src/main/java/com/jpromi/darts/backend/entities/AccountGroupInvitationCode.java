package com.jpromi.darts.backend.entities;

import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Random;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class AccountGroupInvitationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account inviter;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_group_id")
    private AccountGroup accountGroup;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    @Builder.Default
    private Long maxUses = 0L;

    @Column(nullable = true)
    @Builder.Default
    private Instant expirationDate = null;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDisabled = false;

    @Column(nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

}
