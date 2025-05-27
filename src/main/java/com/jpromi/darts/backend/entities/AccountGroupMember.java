package com.jpromi.darts.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "account_group_id"})
)
@Getter
@Setter
public class AccountGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_group_id")
    private AccountGroup accountGroup;

    private Boolean isOwner;
    private Boolean isAdmin;

    @OneToOne
    @JoinColumn(name = "invitation_account_id")
    @JsonIgnore
    private AccountGroupInvitationAccount invitationAccount;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "invitation_code_id")
    @JsonIgnore
    private AccountGroupInvitationCode invitationCode;


}
