package com.blps.lab1.controllers;

import java.io.IOException;
import java.net.URI;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blps.lab1.model.services.PictureService;
import com.blps.lab1.model.services.PictureService.GetResult;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.http.MediaType;

@RestController
@RequiredArgsConstructor
public class PicturesController {
    private final PictureService pictureService;

    @GetMapping("/pictures/{name}")
    public ResponseEntity<?> getMethodName(@PathVariable String name) {

        GetResult result;
        try {
            result = pictureService.getPicture(name);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to get picture");
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(result.getMimeType()))
                .body(result.getImageBytes());

    }

    @PostMapping("/pictures")
    public ResponseEntity<?> postMethodName(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        String newFileName;
        try {
            newFileName = pictureService.savePicture(file);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to upload file");
        }

        URI location = URI.create("/pictures/" + newFileName);
        return ResponseEntity.created(location).build();
    }

}
