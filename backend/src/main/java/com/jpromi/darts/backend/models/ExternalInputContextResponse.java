package com.jpromi.darts.backend.models;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalInputContextResponse {
    private UUID groupUuid;
    private UUID locationUuid;
    private UUID activeGameUuid;
}
