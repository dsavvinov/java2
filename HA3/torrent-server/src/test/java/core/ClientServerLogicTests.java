package core;

import core.client.Client;
import core.server.Server;
import database.DatabaseProvider;
import database.FileEntity;
import io.Logger;
import net.queries.responses.ListResponse;
import net.queries.responses.SourcesResponse;
import net.queries.responses.UpdateResponse;
import net.queries.responses.UploadResponse;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import utils.Constants;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DatabaseProvider.class)
public class ClientServerLogicTests {
    private final Logger logger = new BlackholeLogger();
    private final ClientDBMock clientdb = new ClientDBMock();
    private final ServerDBMock serverDBMock = new ServerDBMock();
    private Client tester;
    private final Server executor = new Server(logger);
    private Thread serverThread;

    @Before
    public void prepare() throws IOException {
        clientdb.seededFiles.add(ClientDBMock.file1);
        clientdb.seededFiles.add(ClientDBMock.file3);

        PowerMockito.mockStatic(DatabaseProvider.class);
        PowerMockito.when(DatabaseProvider.getClientDB()).thenReturn(clientdb);
        PowerMockito.when(DatabaseProvider.getServerDB()).thenReturn(serverDBMock);
        // Set BLOCK_SIZE to 10 for testing
        Whitebox.setInternalState(Constants.class, "BLOCK_SIZE", 10);

        tester = new Client((short) 10000, System.getProperty("user.dir"), logger);

        serverThread = new Thread(() -> {
            try {
                executor.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Give server thread some time to start
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) { }

        tester.initClient();
    }


    @Test
    public void testListQuery() {
        ListResponse actual = tester.executeListCommand();

        ListResponse expected = new ListResponse();
        // As per defined in ServerDBMock
        expected.addAll(Arrays.asList(
                new ListResponse.ListResponseItem(0, "file1", 10),
                new ListResponse.ListResponseItem(1, "file2", 42),
                new ListResponse.ListResponseItem(2, "file3", 228),
                new ListResponse.ListResponseItem(3, "file4", 1337)

        ));

        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateQuery() {
        UpdateResponse updateResponse = tester.executeUpdateCommand();

        assertTrue(updateResponse.getStatus());

        // Initially, ServerDB contained 4 files and some dummy users seeding them
        // After update, for tester-peer should appear for file1 (id=0) and file3(id=1),
        // as it is what ClientDBMock returns for listSeededFiles() (see prepare())
        SourcesResponse.Source testerSource = new SourcesResponse.Source((short) 10000, "127.0.0.1");
        assertTrue(serverDBMock.filesToSources.get(0).contains(testerSource));
        assertFalse(serverDBMock.filesToSources.get(1).contains(testerSource));
        assertTrue(serverDBMock.filesToSources.get(2).contains(testerSource));
        assertFalse(serverDBMock.filesToSources.get(3).contains(testerSource));
    }

    @Test
    public void testSourcesQuery() {
        SourcesResponse sourcesResponse = tester.executeSourcesCommand("2");

        assertTrue(sourcesResponse.contains(ServerDBMock.user1));
        assertTrue(sourcesResponse.contains(ServerDBMock.user2));
        assertTrue(sourcesResponse.contains(ServerDBMock.user3));
    }

    @Test
    public void testUploadQuery() throws IOException {
        Path tmpDir = null;
        try {
            // Prepare tmp dir and file
            Path testerRoot = Paths.get(System.getProperty("user.dir"));
            tmpDir = Files.createTempDirectory(testerRoot, null);
            Path fileAbs = tmpDir.resolve("file1");
            Files.createFile(fileAbs);
            Path file = testerRoot.relativize(fileAbs);

            // Fill file with some content
            try (PrintWriter writer = new PrintWriter(fileAbs.toFile())) {
                writer.write("Hello world! Some additional content to surely " +
                        "overcome testing block size of 10 bytes");
            }

            UploadResponse uploadResponse = tester.executeUploadCommand(file.toString());

            // ServerDB contained 4 files, and 1 new was added, so new id should be 4 per
            // implementation of `uploadFile()` in ServerDBMock
            assertEquals(4, uploadResponse.getId());

            FileEntity fileEntity = serverDBMock.filesByID.get(4);
            assertTrue(fileEntity != null);
            assertEquals("file1", fileEntity.getName());
            assertEquals(4, fileEntity.getId());
            assertEquals(file.toFile().length(), fileEntity.getSize());
        } finally {
            if (tmpDir != null) {
                FileUtils.deleteQuietly(tmpDir.toFile());
            }
        }
    }

    @After
    public void teardown() throws InterruptedException {
        tester.shutdown();
        executor.shouldStop = true;

        serverThread.join();
    }
}
