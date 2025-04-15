package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.File;
import com.jpromi.darts.backend.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("FileController")
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<File> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            java.io.File tempFile = java.io.File.createTempFile("b2h-darts_", file.getOriginalFilename());
            file.transferTo(tempFile);

            File savedFile = fileService.saveFile(tempFile);
            tempFile.delete();

            return ResponseEntity.ok(savedFile);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

}
