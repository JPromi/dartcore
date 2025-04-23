package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FileResponse {
    private UUID uuid;
    private String filename;
    private String url;
}
