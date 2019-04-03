package fileuploader.storage;

import fileuploader.storage.interfaces.StorageService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public abstract class BaseStorageService implements StorageService {

    protected abstract void init();

    @Override
    public abstract Stream<Path> list();

    @Override
    public abstract void save(Path filename, InputStream data);

    @Override
    public abstract InputStream load(Path filename);

    @Override
    public abstract void delete(Path filename);

}
