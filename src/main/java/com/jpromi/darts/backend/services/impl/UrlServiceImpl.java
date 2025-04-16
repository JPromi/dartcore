package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlServiceImpl implements UrlService {

    @Value("${com.jpromi.darts.api.domain}")
    private String apiDomain;

    @Override
    public String toPublicUrl(String privateUrl) {
        return apiDomain + privateUrl;
    }
}
