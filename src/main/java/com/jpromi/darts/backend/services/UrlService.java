package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.File;

public interface UrlService {
    String toPublicUrl(String privateUrl);
    String toPublicUrl(File file);
    String toPublicUrl(File file, String defaultUrl);
}
