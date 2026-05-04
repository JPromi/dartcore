package com.jpromi.darts.backend.services.impl;

import com.jpromi.darts.backend.entities.File;
import com.jpromi.darts.backend.repositories.FileRepository;
import com.jpromi.darts.backend.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
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

    @Override
    public java.io.File getFileByUuidAndName(String uuid, String name) {
        Optional<File> fileOptional = fileRepository.findByUuidAndNameAndIsDeletedFalse(UUID.fromString(uuid), name);
        if (fileOptional.isPresent()) {
            File file = fileOptional.get();

            java.io.File fileGet = _getFile(file.getPath());

            return fileGet;
        } else {
            return null;
        }
    }

    @Override
    public File getFileByUuid(UUID uuid) {
        Optional<File> fileOptional = fileRepository.findByUuidAndIsDeletedFalse(uuid);
        if (fileOptional.isPresent()) {
            return fileOptional.get();
        } else {
            return null;
        }
    }

    @Override
    public void deleteFile(UUID uuid) {
        Optional<File> fileOptional = fileRepository.findByUuidAndIsDeletedFalse(uuid);

        if (fileOptional.isPresent()) {
            File file = fileOptional.get();
            java.io.File fileGet = _getFile(file.getPath());

            if (fileGet != null) {
                _deleteFile(fileGet);
            }

            file.setIsDeleted(true);
            fileRepository.save(file);
        }
    }

    @Override
    public java.io.File scaleImage(java.io.File file, long max) {
        try {
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null || !mimeType.startsWith("image/")) {
                throw new IllegalArgumentException("Unsupported file type: " + mimeType);
            }

            BufferedImage originalImage = ImageIO.read(file);
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            if (originalWidth <= max && originalHeight <= max) {
                return file;
            }

            float scale = Math.min((float) max / originalWidth, (float) max / originalHeight);
            int scaledWidth = Math.round(originalWidth * scale);
            int scaledHeight = Math.round(originalHeight * scale);

            Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(scaledWidth, scaledHeight, originalImage.getType());

            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            String extension = getFileExtension(file.getName());
            ImageIO.write(resizedImage, extension, file);

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return file;
        }
    }

    private String getFileExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return (index > 0 && index < filename.length() - 1) ? filename.substring(index + 1).toLowerCase() : "png";
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

    private java.io.File _getFile(String pathSingleFile) {
        Path path = Path.of(filePath + pathSingleFile);
        if (Files.exists(path)) {
            return path.toFile();
        } else {
            return null;
        }
    }

    private void _deleteFile(java.io.File file) {
        if (file.exists()) {
            file.delete();
        }
    }


}
