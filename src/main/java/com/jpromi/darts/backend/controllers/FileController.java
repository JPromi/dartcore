package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.File;
import com.jpromi.darts.backend.mapper.FileResponseMapper;
import com.jpromi.darts.backend.models.FileResponse;
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

    @Autowired
    private FileResponseMapper fileResponseMapper;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            java.io.File tempFile = java.io.File.createTempFile("b2h-darts_", file.getOriginalFilename());
            file.transferTo(tempFile);

            File savedFile = fileService.saveFile(tempFile);
            tempFile.delete();

            return ResponseEntity.ok(fileResponseMapper.fromFile(savedFile));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> uploadImage(@RequestPart("file") MultipartFile file, @RequestParam(name = "size", defaultValue = "1920") Long max) {
        if(max > 1920) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            java.io.File tempFile = java.io.File.createTempFile("b2h-darts_", file.getOriginalFilename());
            file.transferTo(tempFile);

            // scale image
            java.io.File scaledFile = fileService.scaleImage(tempFile, max);
            System.out.println("Scaled file: " + scaledFile.getAbsolutePath());

            File savedFile = fileService.saveFile(scaledFile);
            tempFile.delete();

            return ResponseEntity.ok(fileResponseMapper.fromFile(savedFile));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

}
