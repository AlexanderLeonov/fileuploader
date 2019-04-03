package fileuploader.storage.exceptions;

import java.nio.file.Path;

public class FileNotFoundStorageException extends StorageException {

    public FileNotFoundStorageException(String filename) {
        super(String.format("File \"%s\" not found", filename));
    }

    public FileNotFoundStorageException(Path filename) {
        super(String.format("File \"%s\" not found", filename));
    }

    public FileNotFoundStorageException(String filename, Throwable cause) {
        super(String.format("File \"%s\" not found", filename), cause);
    }

    public FileNotFoundStorageException(Path filename, Throwable cause) {
        super(String.format("File \"%s\" not found", filename), cause);
    }

}
