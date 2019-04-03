package fileuploader.controllers;

import fileuploader.storage.exceptions.FileNotFoundStorageException;
import fileuploader.storage.exceptions.StorageException;
import fileuploader.storage.interfaces.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FilesController {

    private Logger logger = LoggerFactory.getLogger(FilesController.class);

    private final StorageService storageService;

    @Autowired
    public FilesController(@Qualifier("DefaultStorageService") StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listFiles() {

        // just some very primitive request logging, in real application I'd use some kind of request interceptor if I needed it
        logger.info("listFiles()");

        // retrieving list of available files
        List<String> list;

        try {
            list = storageService
                    .list()
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (StorageException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error enumerating files", e);
        }

        logger.info(String.format("Number of files found: %d", list.size()));

        return ResponseEntity
                .ok()
                .body(list);

    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {

        logger.info("uploadFiles()");

        if (files == null) {
            // there may be something wrong with parsing of the request
            // this is not really expected input, so telling caller that there's something wrong.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file collection provided");
        }

        logger.info(String.format("count = %d", files.length));

        // going through list of files provided by caller
        for (MultipartFile file : files) {

            // getting file name
            String filename = file.getOriginalFilename();

            // checking that it's not empty
            // though not validating it here any further - see below
            if (filename == null || StringUtils.trimWhitespace(filename).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One of files to save has empty name.");
            }

            Path path;

            // checking if file name is valid
            try {
                path = Paths.get(filename);
            } catch (InvalidPathException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Invalid file name: \"%s\"", filename), e);
            }

            InputStream dataStream;

            try {
                // trying to get contents of the file
                dataStream = file.getInputStream();
            } catch (IOException e) {
                // something went wrong
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Could not read content of the file \"%s\"", filename), e);
            }

            // storing contents of the file.
            try {
                logger.info(String.format("Saving file \"%s\"...", path.toString()));
                storageService.save(path, dataStream);
                logger.info(String.format("File \"%s\" saved successfully.", path.toString()));
            } catch (StorageException e) {
                // most likely that previous errors have happened due to the client provided us something wrong, therefore the request was bad
                // here it is another thing - we got all the required data from the client successfully but failed to do our job,
                // so reporting our failure to the client in a different way
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error saving file \"%s\"", filename), e);
            }

        }

        logger.info("All files are saved successfully.");

        // if we're still here then everything up there went fine.
        return ResponseEntity.ok().build();

    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename) {

        logger.info("downloadFile()");

        // checking if we have file name
        // again, not checking if the file name is correct
        if (filename == null || StringUtils.trimWhitespace(filename).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty file name");
        }

        logger.info(String.format("fileName = \"%s\"", filename));

        // checking if file name is valid
        Path path;
        try {
            path = Paths.get(filename);
        } catch (InvalidPathException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Invalid file name: \"%s\"", filename), e);
        }

        // reading contents of the file
        InputStream stream;
        try {
            stream = storageService.load(path);
            logger.info(String.format("File \"%s\" retrieved successfully.", path.toString()));
        } catch (FileNotFoundStorageException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("File not found: \"%s\"", filename), e);
        } catch (StorageException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error loading file \"%s\"", filename), e);
        }

        // creating resource for returning to the client
        Resource content = new InputStreamResource(stream);

        // composing content-disposition header
        String contentDispositionHeader = String.format("attachment; filename=\"%s\"", path.getFileName().toString());

        logger.info(String.format("Setting content-disposition header: %s", contentDispositionHeader));

        // returning successful response
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionHeader)
                .body(content);

    }

    @DeleteMapping("/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {

        logger.info(String.format("deleteFile(\"%s\")", filename));

        // checking if we have file name
        if (filename == null || StringUtils.trimWhitespace(filename).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty file name");
        }

        logger.info(String.format("fileName = \"%s\"", filename));

        // checking if file name is valid
        Path path;
        try {
            path = Paths.get(filename);
        } catch (InvalidPathException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Invalid file name: \"%s\"", filename), e);
        }

        // deleting file
        try {
            storageService.delete(path);
            logger.info(String.format("File \"%s\" deleted successfully.", path.toString()));
        } catch (FileNotFoundStorageException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("File not found: \"%s\"", filename), e);
        } catch (StorageException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error deleting file \"%s\"", filename), e);
        }

        // we got here which means we're all good, responding with "ok".
        return ResponseEntity.ok().build();

    }

}
