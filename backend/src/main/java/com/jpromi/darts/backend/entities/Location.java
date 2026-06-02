package com.jpromi.darts.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

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

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LocationScreen> screens;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LocationClient> clients;

    public List<LocationScreen> getActiveScreens() {
        List<LocationScreen> activeScreens = new ArrayList<>();

        for  (LocationScreen screen : screens) {
            if (!screen.getIsTmp()) {
                activeScreens.add(screen);
            }
        }

        return activeScreens;
    }

    public List<LocationClient> getActiveClients() {
        List<LocationClient> activeClients = new ArrayList<>();

        for  (LocationClient client : clients) {
            if (!client.getIsTmp()) {
                activeClients.add(client);
            }
        }

        return activeClients;
    }

}
