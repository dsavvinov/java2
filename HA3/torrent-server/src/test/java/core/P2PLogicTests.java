package core;

import core.client.Client;
import core.server.Server;
import database.DatabaseProvider;
import database.FileEntity;
import database.client.FilePart;
import io.Logger;
import net.queries.responses.StatResponse;
import org.apache.commons.codec.digest.DigestUtils;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DatabaseProvider.class)
public class P2PLogicTests {
    private final Logger logger = new BlackholeLogger();
    private final ClientDBMock clientdb = new ClientDBMock();
    private final ServerDBMock serverDBMock = new ServerDBMock();
    private Path testerRoot;
    private Path executorRoot;
    private Client executor;
    private Client tester;
    private final Server server = new Server(logger);
    private Thread serverThread;

    @Before
    public void prepare() throws IOException {
        FilePart fp1 = new FilePart(1, 0, 10);
        FilePart fp2 = new FilePart(1, 10, 10);
        FilePart fp3 = new FilePart(1, 20, 10);
        FilePart fp4 = new FilePart(1, 30, 10);
        FilePart fp5 = new FilePart(1, 40, 3);

        clientdb.ownedParts.put(1, new ArrayList<>(Arrays.asList(fp1, fp4, fp5)));

        PowerMockito.mockStatic(DatabaseProvider.class);
        PowerMockito.when(DatabaseProvider.getClientDB()).thenReturn(clientdb);
        PowerMockito.when(DatabaseProvider.getServerDB()).thenReturn(serverDBMock);
        // Set BLOCK_SIZE to 10 for testing
        Whitebox.setInternalState(Constants.class, "BLOCK_SIZE", 10);

        testerRoot = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), null);
        executorRoot = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), null);

        tester = new Client((short) 10000, testerRoot.toString(), logger);
        executor = new Client((short) 9999, executorRoot.toString(), logger);

        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Give server thread some time to start
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) { }

        executor.initClient();

        // Give server thread some time to start
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) { }

        tester.initClient();
    }

    @Test
    public void testStatQuery() {
        StatResponse statResponse = tester.executeStatCommand("localhost", "9999", "1");

        int[] actualParts = statResponse.getParts();
        int[] expectedParts = new int[]{ 0, 3, 4 };

        assertArrayEquals(expectedParts, actualParts);
    }

    @Test
    public void testGetQuery() throws IOException {
        // Prepare tmp dir and file
        Path fileAbs = executorRoot.resolve("file1");
        Files.createFile(fileAbs);
        Path file = executorRoot.relativize(fileAbs);

        // Fill file with some content
        try (PrintWriter writer = new PrintWriter(fileAbs.toFile())) {
            writer.write("Hello world! Some additional content to surely " +
                    "overcome testing block size of 10 bytes");
        }

        // Add that file to Executor's db
        FileEntity fe = new FileEntity(10, file.getFileName().toString(), file.toFile().length());
        fe.setLocalPath(file.toString());
        clientdb.addFile(fe);
        clientdb.addAllPartsOfFile(fe);

        // Execute get commands
        tester.executeGetCommand("localhost", "9999", "10", "0");
        tester.executeGetCommand("localhost", "9999", "10", "1");

        Path downloadedFile = testerRoot.resolve(file);

        String baseFileMD5;
        String downloadedFileMD5;

        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            baseFileMD5 = DigestUtils.md5Hex(fis);
        }

        try (FileInputStream fis = new FileInputStream(downloadedFile.toFile())) {
            downloadedFileMD5 = DigestUtils.md5Hex(fis);
        }

        assertEquals(baseFileMD5, downloadedFileMD5);
    }

    @After
    public void teardown() throws InterruptedException, IOException {
        tester.shutdown();
        executor.shutdown();
        serverThread.interrupt();
        server.shouldStop = true;

        serverThread.join();

        FileUtils.deleteDirectory(executorRoot.toFile());
        FileUtils.deleteDirectory(testerRoot.toFile());
    }
}
