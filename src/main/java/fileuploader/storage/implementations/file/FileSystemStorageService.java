package fileuploader.storage.implementations.file;

import fileuploader.storage.BaseStorageService;
import fileuploader.storage.exceptions.FileNotFoundStorageException;
import fileuploader.storage.exceptions.StorageException;
import fileuploader.storage.implementations.file.config.FileSystemStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service("file-storage")
public class FileSystemStorageService extends BaseStorageService {

    private Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(FileSystemStorageProperties fileSystemStorageProperties) {
        this.rootLocation = Paths.get(fileSystemStorageProperties.getLocation());
    }

    @Override
    protected void init() throws StorageException {

        logger.debug("init()");

        try {
            logger.debug(String.format("Attempting to create directory for storage: \"%s\"", rootLocation));
            Files.createDirectories(rootLocation);
            logger.debug("Storage initialized successfully.");
        } catch (IOException e) {
            logger.error(String.format("Failed to create directory for storage: \"%s\"", rootLocation), e);
            throw new StorageException("Cannot initialize storage", e);
        }

    }

    @Override
    public Stream<Path> list() throws StorageException {

        logger.debug("list()");

        try {

            logger.debug("Retrieving list of files");

            return Files
                    // I know this is going to produce whole stream of all files and folders before it will be filtered
                    // that's fine for this toy project.
                    .walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);

        } catch (IOException e) {
            throw new StorageException("Cannot retrieve list of stored files", e);
        }

    }

    @Override
    public void save(Path filename, InputStream data) throws StorageException {

        logger.debug("save()");

        String file = StringUtils.cleanPath(filename.getFileName().toString());

        try {

            // some basic checks
            // should actually check for more things - for example, if the file name contains invalid characters
            if (file.isEmpty() || file.trim().isEmpty()) {
                throw new StorageException("File name must be provided!");
            }

            Path path = this.rootLocation.resolve(file);
            logger.debug(String.format("Saving file \"%s\"...", path.toString()));
            Files.copy(data, path, StandardCopyOption.REPLACE_EXISTING);
            logger.debug(String.format("File \"%s\" saved successfully.", path.toString()));

        } catch (IOException e) {
            throw new StorageException("Cannot store file " + file, e);
        }

    }

    @Override
    public InputStream load(Path filename) throws StorageException {

        logger.debug("load()");

        Path path = rootLocation.resolve(filename);
        FileInputStream stream;

        try {

            if (!Files.exists(path)) {
                throw new FileNotFoundStorageException(filename);
            }

            if (!Files.isReadable(path) || !Files.isRegularFile(path)) {
                throw new StorageException("Cannot read file " + filename);
            }

            logger.debug(String.format("Loading file \"%s\"...", path.toString()));
            stream = new FileInputStream(new File(path.toString()));
            logger.debug(String.format("File \"%s\" loaded successfully.", path.toString()));

        } catch (IOException e) {
            throw new StorageException("Cannot read file " + filename, e);
        }

        return stream;

    }

    @Override
    public void delete(Path filename) throws StorageException {

        logger.debug("delete()");

        Path path = this.rootLocation.resolve(filename);

        if (!Files.exists(path)) {
            throw new FileNotFoundStorageException(filename);
        }

        try {
            logger.debug(String.format("Deleting file \"%s\"...", path.toString()));
            Files.delete(path);
            logger.debug(String.format("File \"%s\" deleted successfully.", path.toString()));
        } catch (IOException e) {
            throw new StorageException("Cannot delete file " + filename, e);
        }

    }

}
