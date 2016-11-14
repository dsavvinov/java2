package ru.spbau.mit.server;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import ru.spbau.mit.client.Client;

import java.io.FileInputStream;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class ServerTest {
    @Test
    public void testFileList() throws Exception {
        Path currentFolder = Paths.get(System.getProperty("user.dir"));
        Path resourcesFolder = currentFolder.resolve("build").resolve("resources").resolve("test");
        System.setProperty("user.dir", resourcesFolder.toString());

        Thread serverThread = new Thread(() -> {
            try {
                Server server = new Server();
                server.start();
            } catch (ClosedByInterruptException e) {
                // shut down gracefully
                return;
            } catch (Exception e) {
                // unexpected exception, rethrow
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        Thread.sleep(100);

        Client client = new Client();
        client.connect();
        List<Item> actual = client.getList("./");

        List<String> expected = new ArrayList<>(Arrays.asList(
            ".",
            "./empty_file",
            "./file1",
            "./file2",
            "./file3_with_long_name"
        ));

        assertEquals(expected.size(), actual.size());
        actual.stream().map(Item::getPath).forEach( it -> assertTrue(expected.contains(it)));

        client.shutdown();
        serverThread.interrupt();
        serverThread.join();
    }

    @Test
    public void testFileDownload() throws Exception {
        Path currentFolder = Paths.get(System.getProperty("user.dir"));
        Path resourcesFolder = currentFolder.resolve("build").resolve("resources").resolve("test");
        System.setProperty("user.dir", resourcesFolder.toString());

        Thread serverThread = new Thread(() -> {
            try {
                Server server = new Server();
                server.start();
            } catch (ClosedByInterruptException e) {
                // shut down gracefully
                return;
            } catch (Exception e) {
                // unexpected exception, rethrow
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        Thread.sleep(100);

        Client client = new Client();
        client.connect();
        client.getFile("file3_with_long_name");

        Path downloadPath = resourcesFolder.resolve("file3_with_long_name" + ".download");
        Path sourcePath = resourcesFolder.resolve("storage").resolve("file3_with_long_name");
        assertTrue(Files.exists(downloadPath));

        FileInputStream fis = new FileInputStream(downloadPath.toFile());
        String actualMD5 = DigestUtils.md5Hex(fis);
        fis.close();

        fis = new FileInputStream(sourcePath.toFile());
        String expectedMD5 = DigestUtils.md5Hex(fis);
        fis.close();

        assertEquals(expectedMD5, actualMD5);

        serverThread.interrupt();
        client.shutdown();
        serverThread.join();
    }
}