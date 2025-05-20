package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.File;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlServiceImpl implements UrlService {

    @Value("${com.jpromi.darts.api.domain}")
    private String apiDomain;

    @Override
    public String toPublicUrl(String privateUrl) {
        if (privateUrl == null || privateUrl.isEmpty()) {
            return null;
        } else  {
            return apiDomain + privateUrl;
        }
    }

    @Override
    public String toPublicUrl(File file) {
        if (file == null) {
            return null;
        } else {
            return toPublicUrl(file.getRealPath());
        }
    }
}
