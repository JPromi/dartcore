package com.jpromi.darts.backend.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private long totalPages;
    private Boolean isFirst;
    private Boolean isLast;
    private Boolean isEmpty;
}
