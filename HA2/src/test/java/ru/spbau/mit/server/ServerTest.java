package ru.spbau.mit.server;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import ru.spbau.mit.client.Client;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        Thread.sleep(100);

        Client client = new Client();
        client.connect();
        ArrayList<String> actual = client.getList();

        ArrayList<String> expected = new ArrayList<>();
        expected.add("empty_file");
        expected.add("file1");
        expected.add("file2");
        expected.add("file3_with_long_name");

        assertEquals(actual.size(), expected.size());
        expected.forEach( it -> assertTrue(actual.contains(it)) );

        client.shutdown();
        serverThread.interrupt();
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
            } catch (Exception e) {
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

        fis = new FileInputStream(sourcePath.toFile());
        String expectedMD5 = DigestUtils.md5Hex(fis);

        assertEquals(expectedMD5, actualMD5);
    }
}