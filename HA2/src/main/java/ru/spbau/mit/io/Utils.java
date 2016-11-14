package ru.spbau.mit.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    public static File createFile(String file) throws IOException {
        Path currentFolder = Paths.get(System.getProperty("user.dir"));
        Path filePath = currentFolder.resolve(file);

        if (Files.exists(filePath)) {
            throw new IOException("File " + filePath + " already exists!");
        }
        Files.createDirectories(filePath.getParent());
        Files.createFile(filePath);

        return filePath.toFile();
    }
}
