package fileuploader.controllers;

import fileuploader.storage.exceptions.FileNotFoundStorageException;
import fileuploader.storage.exceptions.StorageException;
import fileuploader.storage.interfaces.StorageService;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FilesControllerTest {

    @Mock
    StorageService storageServiceMock;

    @InjectMocks
    FilesController controller;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetFileListSuccess() throws StorageException {
        // data and mock setup
        when(storageServiceMock.list()).thenReturn(Stream.of("file1.txt", "file2.txt").map(s -> Paths.get(s)));

        // perform test
        ResponseEntity<?> responseEntity = controller.listFiles();

        // assertion
        assertThat("response contains unexpected list of files", responseEntity.getBody(), is(Arrays.asList("file1.txt", "file2.txt")));
    }

    @Test
    public void testGetFileListFail() throws StorageException {
        // data and mock setup
        when(storageServiceMock.list()).thenThrow(new StorageException("test message"));

        // setting up exception checking
        exception.expect(ResponseStatusException.class);
        exception.expectMessage(CoreMatchers.containsString("test message"));

        // perform test
        controller.listFiles();

        // exception should have been thrown
    }

    @Test
    public void testDownloadFileSuccess() throws StorageException, IOException {
        // data and mock setup
        byte[] data = new byte[]{1, 2, 3};
        byte[] excp = new byte[]{1, 2, 3, 4};

        // setup mock to return test data
        when(storageServiceMock.load(any())).thenReturn(new ByteArrayInputStream(data));

        // perform test
        ResponseEntity<?> responseEntity = controller.downloadFile("file123.txt");

        // now let's figure out what we got
        // this actually should have been several separate tests each checking one specific thing
        // I'm cutting corners a bit by checking everything about the response in a single test

        // first check whether there's response
        assertNotNull("response cannot be null", responseEntity);

        Object responseBody = responseEntity.getBody();

        // check if response body is present
        assertNotNull("response body cannot be null", responseBody);
        // check if response body has correct type
        assertTrue(String.format("unexpected response body type - %s", responseBody.getClass().getName()), responseBody instanceof Resource);

        // now let's try to read resource content
        ReadableByteChannel ch = ((Resource) responseBody).readableChannel();
        ByteBuffer buf = ByteBuffer.allocate(3);

        // trying to read 3 numbers
        int read = ch.read(buf);

        // check that there was at least 3 numbers
        assertEquals("data in response has wrong length", 3, read);

        // compare contents of the buffer
        assertThat("data in response is incorrect", buf.array(), is(data));

        // check that headers are correct
        assertTrue("there is no content-disposition header", responseEntity.getHeaders().containsKey(HttpHeaders.CONTENT_DISPOSITION));
        assertThat("content-disposition header is invalid", responseEntity.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION),
                is(Collections.singletonList("attachment; filename=\"file123.txt\"")));

        // making sure that was all the data in the response
        buf.rewind();
        read = ch.read(buf);
        assertEquals("response body contains more data than expected", read, -1);
    }

    @Test
    public void testDownloadFileFail() throws StorageException {
        // data and mock setup
        when(storageServiceMock.load(any())).thenThrow(new StorageException("test message"));

        // setting up exception checking
        exception.expect(ResponseStatusException.class);
        exception.expectMessage(CoreMatchers.containsString("test message"));

        // perform test
        controller.downloadFile("file123.txt");
    }

    @Test
    public void testDownloadFileNotFound() throws StorageException {
        // data and mock setup
        when(storageServiceMock.load(any())).thenThrow(new FileNotFoundStorageException("test message"));

        // setting up exception checking
        exception.expect(ResponseStatusException.class);
        exception.expectMessage(CoreMatchers.containsString("test message"));

        // perform test
        controller.downloadFile("file123.txt");
    }

    // ... here goes another dozen of similar tests for the rest of controller methods

}
