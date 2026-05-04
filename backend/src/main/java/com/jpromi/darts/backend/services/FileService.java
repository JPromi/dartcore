package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.entities.File;

import java.util.UUID;

public interface FileService {
    File saveFile(java.io.File file);
    File getFileById(Long id);
    java.io.File getFileByUuidAndName(String uuid, String name);
    File getFileByUuid(UUID uuid);
    void deleteFile(UUID uuid);
    java.io.File scaleImage(java.io.File file, long max);
}
