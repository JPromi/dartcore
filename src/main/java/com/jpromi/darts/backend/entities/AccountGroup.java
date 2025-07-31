package com.jpromi.darts.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class AccountGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String description;

    @ManyToOne
    private File avatar;

    @ManyToOne
    private File banner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accountGroup")
    private List<AccountGroupMember> members;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Location> locations;

    @Column(nullable = true)
    @Builder.Default
    private Boolean isPublic = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accountGroup")
    private List<AccountGroupInvitationCode> invitationCodes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accountGroup")
    private List<AccountGroupInvitationAccount> invitations;

    @Column(nullable = true)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Transient
    public List<Account> getAccounts() {
        return members.stream().map(AccountGroupMember::getAccount).toList();
    }

}
