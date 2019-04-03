package fileuploader.storage.memory;

import fileuploader.storage.exceptions.FileNotFoundStorageException;
import fileuploader.storage.exceptions.StorageException;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MemoryStorageServiceTest {

    private MemoryStorageServiceWrapper service;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void prepareTestData() {
        service = new MemoryStorageServiceWrapper();
        service.initializeWrapper();
    }

    @After
    public void testCleanup() throws IOException {
    }

    @Test
    public void testSaveFile() throws StorageException {
        // setup
        Path file = Paths.get("test3.bin");
        ByteArrayInputStream stream = new ByteArrayInputStream(getTrashData());

        // act
        service.save(file, stream);

        // assertions
        List<Path> list = service.list().collect(Collectors.toList());

        assertThat(list, is(Collections.singletonList(file)));
    }

    @Test
    public void testLoadFile() throws IOException, StorageException {
        // setup
        Path file = Paths.get("test4.bin");
        byte[] expected = getTrashData();
        service.save(file, new ByteArrayInputStream(expected));

        // act
        InputStream stream = service.load(file);

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
        service.load(file);
    }

    @Test
    public void testDeleteFile() throws IOException, StorageException {

        // setup
        Path file = Paths.get("test5.bin");
        byte[] expected = getTrashData();
        service.save(file, new ByteArrayInputStream(expected));

        // just in case, not really needed

        // act
        service.delete(file);

        // assertions
        List<Path> list = service.list().collect(Collectors.toList());
        assertEquals(0, list.size());

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

}
