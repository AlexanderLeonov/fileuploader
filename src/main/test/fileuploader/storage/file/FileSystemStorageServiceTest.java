package fileuploader.storage.file;

import fileuploader.storage.exceptions.FileNotFoundStorageException;
import fileuploader.storage.exceptions.StorageException;
import fileuploader.storage.implementations.file.config.FileSystemStorageProperties;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FileSystemStorageServiceTest {

    private static FileSystemStorageProperties properties;
    private static Path tmpDir;

    private FileSystemStorageServiceWrapper service;
    private InputStream stream;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void init() {
        // java.io.tmpdir in certain cases can point to a Windows dir
        // in which case it's going to be a picnic cleaning it up :)
        // so as a safety measure adding our own directory on top of whatever is provided by OS
        // this one we always can just delete... if it was created in the first place...
        tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), "file-storage-test");
        properties = new FileSystemStorageProperties();
        properties.setLocation(tmpDir.toString());
    }

    @Before
    public void prepareTestData() {
        service = new FileSystemStorageServiceWrapper(properties);
        service.initializeWrapper();
    }

    @After
    public void testCleanup() throws IOException {

        service = null;

        try {
            stream.close();
        } catch (Exception e) {
            // ignoring anything that could happen
            // stream could have been not created
            // it could have not been opened or is already closed
            // we don't really care if something went wrong, it's best-we-can-do effort.
        }

        stream = null;

        for (Path path : Files.walk(tmpDir, 8)
                // we want delete files first and then directories including our root
                .sorted(Comparator.reverseOrder())
                // "freezing" list in order to not follow changes in the file tree
                .collect(Collectors.toList())) {
            Files.delete(path);
        }

    }

    @Test
    public void testInit() throws StorageException {
        // assertions
        assertTrue(Files.exists(tmpDir));
    }

    @Test
    public void testListFiles() throws IOException, StorageException {
        // setup
        createTempFile("test1.bin", getTrashData());
        createTempFile("test2.bin", getTrashData());

        List<Path> expected = Arrays.asList(Paths.get("test1.bin"), Paths.get("test2.bin"));

        // act
        List<Path> actual = service.list().collect(Collectors.toList());

        // assertions
        assertThat(actual, is(expected));
    }

    @Test
    public void testSaveFile() throws StorageException {
        // setup
        Path file = Paths.get("test3.bin");
        stream = new ByteArrayInputStream(getTrashData());

        // act
        service.save(file, stream);

        // assertions
        assertTrue(Files.exists(tmpDir.resolve(file)));
    }

    @Test
    public void testLoadFile() throws IOException, StorageException {
        // setup
        Path file = Paths.get("test4.bin");
        byte[] expected = getTrashData();
        createTempFile(file, expected);

        // act
        stream = service.load(file);

        // assertions
        byte[] actual = new byte[expected.length];
        int read = stream.read(actual, 0, expected.length);
        assertEquals(read, expected.length);
        assertThat(actual, is(expected));

        // checking there's no extra data
        int readAfter = stream.read();
        assertEquals(readAfter, -1);
    }

    @Test
    public void testLoadFileNotFound() throws StorageException, IOException {
        // setup
        Path file = Paths.get("test4.bin");

        // setting up exception checking
        exception.expect(FileNotFoundStorageException.class);
        exception.expectMessage(CoreMatchers.containsString("File \"test4.bin\" not found"));

        // perform test
        stream = service.load(file);
    }

    @Test
    public void testDeleteFile() throws IOException, StorageException {
        // setup
        Path file = Paths.get("test5.bin");
        byte[] expected = getTrashData();
        createTempFile(file, expected);

        // just in case, not really needed
        assertTrue(Files.exists(tmpDir.resolve(file)));

        // act
        service.delete(file);

        // assertions
        assertFalse(Files.exists(tmpDir.resolve(file)));
    }

    @Test
    public void testDeleteFileNotFound() throws StorageException {
        // setup
        Path file = Paths.get("test5.bin");

        // setting up exception checking
        exception.expect(FileNotFoundStorageException.class);
        exception.expectMessage(CoreMatchers.containsString("File \"test5.bin\" not found"));

        // perform test
        service.delete(file);
    }

    // some service stuff

    private byte[] getTrashData() {
        byte[] data = new byte[256];
        Random gen = new Random();
        gen.nextBytes(data);
        return data;
    }

    private void createTempFile(String name, byte[] data) throws IOException {
        createTempFile(Paths.get(name), data);
    }

    private void createTempFile(Path name, byte[] data) throws IOException {
        Files.write(tmpDir.resolve(name), data);
    }

}
