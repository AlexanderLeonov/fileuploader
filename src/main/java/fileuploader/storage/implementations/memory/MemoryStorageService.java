package fileuploader.storage.implementations.memory;

import fileuploader.storage.BaseStorageService;
import fileuploader.storage.exceptions.FileNotFoundStorageException;
import fileuploader.storage.exceptions.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service("memory-storage")
public class MemoryStorageService extends BaseStorageService {

    private Logger logger = LoggerFactory.getLogger(MemoryStorageService.class);

    private Map<Path, byte[]> storage;

    @Override
    protected void init() {

        logger.debug("init()");

        storage = new HashMap<>();

    }

    @Override
    public Stream<Path> list() {

        logger.debug("list()");

        return storage.keySet().stream().sorted();

    }

    @Override
    public void save(Path filename, InputStream data) {

        logger.debug("save()");

        byte[] binary;
        try {
            binary = StreamUtils.copyToByteArray(data);
        } catch (IOException e) {
            throw new StorageException("Error copying data from input stream", e);
        }

        storage.put(filename, binary);

    }

    @Override
    public InputStream load(Path filename) {

        logger.debug("load()");

        if (!storage.containsKey(filename)) {
            throw new FileNotFoundStorageException(filename);
        }

        return new ByteArrayInputStream(storage.get(filename));

    }

    @Override
    public void delete(Path filename) {

        logger.debug("delete()");

        if (!storage.containsKey(filename)) {
            throw new FileNotFoundStorageException(filename);
        }

        storage.remove(filename);

    }

}
