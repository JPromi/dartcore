package com.jpromi.darts.backend.mapper;

import com.jpromi.darts.backend.entities.File;
import com.jpromi.darts.backend.models.FileResponse;
import com.jpromi.darts.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileResponseMapper {

    @Autowired
    private UrlService urlService;

    public FileResponse fromFile(File file) {
        if(file != null) {
            return FileResponse.builder()
                    .uuid(file.getUuid())
                    .filename(file.getName())
                    .url(urlService.toPublicUrl(file.getRealPath()))
                    .build();
        } else {
            return null;
        }
    }
}
