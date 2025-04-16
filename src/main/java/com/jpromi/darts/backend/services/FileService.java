package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.File;

public interface FileService {
    File saveFile(java.io.File file);
    File getFileById(Long id);
    java.io.File getFileByUuidAndName(String uuid, String name);
}
