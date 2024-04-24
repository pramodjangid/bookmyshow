package com.example.bookmyshow.controller;

import com.example.bookmyshow.service.SftpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class SftpController {

    @Autowired
    private SftpService sftpService;

    @PostMapping("/")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("remoteFilePath") String remoteFilePath) {
        try {
            File localFile = convertMultiPartToFile(file);
            sftpService.uploadFile(localFile.getAbsolutePath(), remoteFilePath);
            localFile.delete();
            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @GetMapping("/")
    public ResponseEntity<Resource> downloadFile(@RequestParam("remoteFilePath") String remoteFilePath) {
        try {
            String localFilePath = "/path/to/save/downloaded/file";
            sftpService.downloadFile(localFilePath, remoteFilePath);
            Resource resource = new FileSystemResource(localFilePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }
}
