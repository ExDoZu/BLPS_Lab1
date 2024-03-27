package com.blps.lab1.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.http.MediaType;

@RestController
@RequestMapping("/pictures")
public class PictureController {

    @GetMapping("/picture/{name}")
    public ResponseEntity<byte[]> getMethodName(@PathVariable String name) {

        Path path = Paths.get("./uploads/" + name);

        byte[] imageBytes;
        try {
            imageBytes = Files.readAllBytes(path);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);

    }

    @PostMapping("/picture")
    public ResponseEntity<?> postMethodName(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {

            String fileName = file.getOriginalFilename();

            byte[] bytes = file.getBytes();

            String newFileName = java.util.UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf('.'));

            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(new File("./uploads/" + newFileName)));

            stream.write(bytes);
            stream.close();
            return ResponseEntity.ok().body(newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to upload file");
        }
    }

}
