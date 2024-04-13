package com.blps.lab1.model.services;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import java.io.File;
import java.io.FileNotFoundException;

@Service
@RequiredArgsConstructor
public class PictureService {

    public class GetResult {
        private String mimeType;
        private byte[] imageBytes;

        public GetResult(String mimeType, byte[] imageBytes) {
            this.mimeType = mimeType;
            this.imageBytes = imageBytes;
        }

        public String getMimeType() {
            return mimeType;
        }

        public byte[] getImageBytes() {
            return imageBytes;
        }
    }

    public GetResult getPicture(String name) throws IOException {
        String mimeType;
        byte[] imageBytes;
        Path path = Paths.get("./uploads/" + name);
        imageBytes = Files.readAllBytes(path);
        File file = path.toFile();
        Tika tika = new Tika();
        mimeType = tika.detect(file);
        return new GetResult(mimeType, imageBytes);
    }

    public String savePicture(MultipartFile file) throws FileNotFoundException, IOException {
        byte[] bytes = file.getBytes();
        String newFileName = java.util.UUID.randomUUID().toString();
        BufferedOutputStream stream = new BufferedOutputStream(
                new FileOutputStream(new File("./uploads/" + newFileName)));
        stream.write(bytes);
        stream.close();
        return newFileName;
    }

}
