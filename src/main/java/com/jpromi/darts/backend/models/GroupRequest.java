package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GroupRequest {

    private String name;
    private String description;
    private FileResponse avatar;
    private FileResponse banner;
    private Boolean isPublic;
    private List<String> members;

}
