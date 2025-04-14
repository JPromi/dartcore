package com.jpromi.darts.backend.entities;

import com.jpromi.darts.backend.enums.ProfileVisibilityEnum;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private ProfileVisibilityEnum visibility = ProfileVisibilityEnum.PRIVATE;

    @Column(nullable = true)
    private String linkWeb;

    @Column(nullable = true)
    private String linkX;

    @Column(nullable = true)
    private String linkInstagram;

    @Column(nullable = true)
    private String linkFacebook;

    @Column(nullable = true)
    private String linkYoutube;

    @Column(nullable = true)
    private String linkGithub;

    @Column(nullable = true)
    private String linkTwitch;

    @Column(nullable = true)
    private String country;

    @Column(nullable = true)
    private File banner;
}
