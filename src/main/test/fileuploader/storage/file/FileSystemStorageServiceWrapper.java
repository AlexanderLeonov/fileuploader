package fileuploader.storage.file;

import fileuploader.storage.implementations.file.FileSystemStorageService;
import fileuploader.storage.implementations.file.config.FileSystemStorageProperties;

class FileSystemStorageServiceWrapper extends FileSystemStorageService {
    FileSystemStorageServiceWrapper(FileSystemStorageProperties fileSystemStorageProperties) {
        super(fileSystemStorageProperties);
    }

    void initializeWrapper() {
        this.init();
    }
}
