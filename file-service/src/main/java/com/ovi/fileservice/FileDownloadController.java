package com.ovi.fileservice;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

/**
 * Used to return a random file from the input folder.
 */
@RestController
public class FileDownloadController {

    private static final Logger logger = LogManager.getLogger(FileDownloadController.class);

    private final FileMonitor fileMonitor;
    RandomFile randomFile;
    @Autowired
    public FileDownloadController(FileMonitor fileMonitor) {
        this.fileMonitor =  fileMonitor;
    }

    @Autowired
    public void setRandomFile(RandomFile randomFile) {
        this.randomFile = randomFile;
    }

    /**
     * Returns a random file from the input folder.
     *
     * @return ResponseEntity
     */
    @GetMapping("/randomFile")
    public ResponseEntity<?> randomFile() {

        Path file;
        UrlResource urlResource;
        try {
            file = randomFile.getFile();
            logger.info("Random file: " + file.getFileName());
            urlResource = new UrlResource(file.toUri());
        } catch (NoSuchFileException e) {
            logger.error("Invalid file " + e.getMessage());
            return ResponseEntity.notFound().build();
        }catch (InvalidPathException e) {
            logger.error("Invalid folder " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            logger.error("Error returning file: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        String headerValue = "attachment; filename=\"" + file.getFileName() + "\"";
        MediaType mediaType = getMediaType(file);

        if (mediaType.toString().equals(APPLICATION_PDF_VALUE)) {
            checkIfCorrupt(file);
        } else {
            fileMonitor.getPngCounter().increment();
        }

        return ResponseEntity.ok().contentType(mediaType).header(HttpHeaders.CONTENT_DISPOSITION, headerValue).body(urlResource);
    }

    /**
     * Uses an external pdf java library to determine if the pdf is corrupt.
     *
     * @param file Path
     */
    private void checkIfCorrupt(Path file) {

        try {
            PdfReader pdfReader = new PdfReader(file.toFile().getPath());
            PdfTextExtractor.getTextFromPage(pdfReader, 1);
            fileMonitor.getPdfCounter().increment();
        } catch (Exception e) {
            fileMonitor.getInvalidPdfCounter().increment();
            logger.warn(file.getFileName() + " is corrupted");
        }
    }

    /**
     * Extracts the file MediaType
     *
     * @param file Path to the file.
     * @return MediaType
     */
    private MediaType getMediaType(Path file) {
        try {
            return new MediaType(MimeTypeUtils.parseMimeType(Files.probeContentType(file)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
