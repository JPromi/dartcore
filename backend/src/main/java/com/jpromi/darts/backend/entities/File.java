package com.jpromi.darts.backend.entities;

import com.jpromi.darts.backend.services.UrlService;
import com.jpromi.darts.backend.services.impl.UrlServiceImpl;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
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
    private String path;

    @Column(nullable = false)
    @ColumnDefault("true")
    @Builder.Default
    private Boolean isTmporary = true;

    @Column(nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean isDeleted = false;

    public String getRealPath() {
        return "/api/files" + path;
    }
}
