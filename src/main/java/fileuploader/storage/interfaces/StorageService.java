package fileuploader.storage.interfaces;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    Stream<Path> list();
    void save(Path filename, InputStream data);
    InputStream load(Path filename);
    void delete(Path filename);
}
