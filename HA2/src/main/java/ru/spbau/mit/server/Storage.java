package ru.spbau.mit.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

class Storage {
    private static final String STORAGE_FOLDER = "storage";
    private static Path root;

    static void initHere() throws IOException {
        Path curDir = Paths.get(System.getProperty("user.dir"));
        root = curDir.resolve(STORAGE_FOLDER);

        if (Files.notExists(root)) {
            throw new IOException("Storage folder not found!");
        }
    }

    static ArrayList<String> getFilesList() throws IOException {
        return Files.walk(root, 1)
                .filter(it -> !Files.isDirectory(it))
                .map(it -> root.relativize(it))
                .map(Path::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    static Path getAbsolutePath(String fileName) {
        Path filePath = root.resolve(fileName);
        if (Files.notExists(filePath)) {
            return null;
        }
        return filePath;
    }
}
