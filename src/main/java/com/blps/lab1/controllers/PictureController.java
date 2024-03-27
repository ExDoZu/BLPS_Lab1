package com.blps.lab1.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;

@RestController
public class PictureController {

    @GetMapping("/picture/{name}")
    public ResponseEntity<?> getMethodName(@PathVariable String name) {

        Path path = Paths.get("./uploads/" + name);
        String mimeType;
        byte[] imageBytes;
        try {
            imageBytes = Files.readAllBytes(path);
            File file = path.toFile();
            Tika tika = new Tika();
            mimeType = tika.detect(file);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType)).body(imageBytes);

    }

    @PostMapping("/picture")
    public ResponseEntity<?> postMethodName(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            byte[] bytes = file.getBytes();
            String newFileName = java.util.UUID.randomUUID().toString();
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(new File("./uploads/" + newFileName)));
            stream.write(bytes);
            stream.close();

            var response = new HashMap<String, String>();
            response.put("url", newFileName);
            return ResponseEntity.ok().body(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to upload file");
        }
    }

}
