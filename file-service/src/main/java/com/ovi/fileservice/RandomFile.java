package com.ovi.fileservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
@Component
public class RandomFile {
    private final String filesPath;
    @Autowired
    public RandomFile(@Value("${file-service.path}") String filesPath) {
        this.filesPath = filesPath;
    }

    /**
     * Returns a random file from the specified folder.
     *
     * @return Path to the file
     * @throws IOException Throws exception if invalid path.
     */
    public Path getFile() throws IOException {

        Path filesPath = Paths.get(this.filesPath);

        Random random = new Random();
        try (Stream<Path> stream = Files.list(filesPath)) {

            List<Path> files = stream.filter(f -> {
                try {
                    String contentType = Files.probeContentType(f);
                    return contentType != null && (contentType.equals(APPLICATION_PDF_VALUE) || contentType.equals(IMAGE_PNG_VALUE));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
            if (files.size() == 0) {
                throw new InvalidPathException(this.filesPath, "No files");
            }
            return files.get(random.nextInt(files.size()));
        }
    }
}
