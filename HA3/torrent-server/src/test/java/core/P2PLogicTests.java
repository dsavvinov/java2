package core;

import core.p2p.PeerServer;
import core.p2p.PeerService;
import core.p2p.PeerServiceNIOBased;
import database.DatabaseProvider;
import database.FileEntity;
import database.client.FilePart;
import exceptions.InvalidProtocolException;
import io.Logger;
import net.protocols.Peer2PeerProtocol;
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
    private Path testerRoot;
    private Path executorRoot;
    private PeerServer executor;
    private PeerService tester;

    @Before
    public void prepare() throws IOException {
        FilePart fp1 = new FilePart(1, 0, 10);
        FilePart fp2 = new FilePart(1, 10, 10);
        FilePart fp3 = new FilePart(1, 20, 10);
        FilePart fp4 = new FilePart(1, 30, 10);
        FilePart fp5 = new FilePart(1, 40, 3);

        clientdb.ownedParts.put(1, new ArrayList<>(Arrays.asList(fp1, fp4, fp5)));

        // Set BLOCK_SIZE to 10 for testing
        Whitebox.setInternalState(Constants.class, "BLOCK_SIZE", 10);

        testerRoot = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), null);
        executorRoot = Files.createTempDirectory(Paths.get(System.getProperty("user.dir")), null);

        tester = new PeerServiceNIOBased(testerRoot.toString(), new Peer2PeerProtocol(), logger);
        executor = new PeerServer((short) 9999, executorRoot.toString(), logger, clientdb);

        executor.start();

        // Give server thread some time to start
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) { }
    }

    @Test
    public void testStatQuery() throws IOException, InvalidProtocolException {
        StatResponse statResponse = tester.stat("localhost", (short) 9999, 1);

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
            writer.write("Hello world!!!!");
        }

        // Add that file to Executor's db
        FileEntity fe = new FileEntity(10, file.getFileName().toString(), fileAbs.toFile().length());
        fe.setLocalPath(fileAbs.toString());
        clientdb.addFile(fe);
        clientdb.addAllPartsOfFile(fe);

        Path downloadedFile = testerRoot.resolve(file);

        // Execute get commands
        tester.get("localhost", (short) 9999, 10, 0, downloadedFile.toString(), fileAbs.toFile().length());
        tester.get("localhost", (short) 9999, 10, 1, downloadedFile.toString(), fileAbs.toFile().length());

        String baseFileMD5;
        String downloadedFileMD5;

        try (FileInputStream fis = new FileInputStream(fileAbs.toFile())) {
            baseFileMD5 = DigestUtils.md5Hex(fis);
        }

        try (FileInputStream fis = new FileInputStream(downloadedFile.toFile())) {
            downloadedFileMD5 = DigestUtils.md5Hex(fis);
        }

        assertEquals(baseFileMD5, downloadedFileMD5);
    }

    @After
    public void teardown() throws InterruptedException, IOException {
        executor.shutdown();

        FileUtils.deleteDirectory(executorRoot.toFile());
        FileUtils.deleteDirectory(testerRoot.toFile());
    }
}
