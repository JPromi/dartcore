package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.File;
import com.jpromi.darts.backend.repositories.FileRepository;
import com.jpromi.darts.backend.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Value("${com.jpromi.darts.files.path}")
    private String filePath;

    @Override
    public File saveFile(java.io.File file) {

        String fileName = file.getName().replace(" ", "_");

        File fileEntity = new File();
        fileEntity.setUuid(UUID.randomUUID());
        fileEntity.setName(fileName);
        fileEntity.setType(file.getName().substring(file.getName().lastIndexOf(".") + 1));

        String save = _saveFile(file, fileEntity.getUuid().toString(), fileName);

        if(save != null) {
            fileEntity.setPath(save);

            fileRepository.save(fileEntity);

            return fileEntity;
        } else {
            return null;
        }
    }

    @Override
    public File getFileById(Long id) {
        // Implement the logic to retrieve the file by ID
        return new File(); // Placeholder return statement
    }

    private String _saveFile(java.io.File file, String uuid, String fileName) {
        try {
            Path path = Path.of(filePath);
            Path pathUuid = path.resolve(uuid);

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            if (!Files.exists(pathUuid)) {
                Files.createDirectories(pathUuid);
            }

            Path targetPath = pathUuid.resolve(fileName);

            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/" + path.relativize(targetPath).toString().replace("\\", "/");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
