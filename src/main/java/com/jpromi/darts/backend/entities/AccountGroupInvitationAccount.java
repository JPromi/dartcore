package com.jpromi.darts.backend.entities;

import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class AccountGroupInvitationAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "inviter_id")
    private Account inviter;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_group_id")
    private AccountGroup accountGroup;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAdmin = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InvitationStatusAccountEnum status = InvitationStatusAccountEnum.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

}
