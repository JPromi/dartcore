package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.Account;
import com.jpromi.darts.backend.entities.AccountGroup;
import com.jpromi.darts.backend.entities.AccountGroupMember;
import com.jpromi.darts.backend.models.GroupLightResponse;
import com.jpromi.darts.backend.models.PageResponse;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageResponseMapper {

    public <T> PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isEmpty(page.isEmpty())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .page(page.getNumber())
                .size(page.getSize())
                .build();
    }

}
