package fileuploader.storage.memory;

import fileuploader.storage.implementations.memory.MemoryStorageService;

class MemoryStorageServiceWrapper extends MemoryStorageService {
    void initializeWrapper() {
        this.init();
    }
}
