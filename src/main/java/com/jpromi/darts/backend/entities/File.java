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
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted;

    public String getUrl() {
        return "/api/v1/file/" + uuid;
    }
}
