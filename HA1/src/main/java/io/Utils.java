package io;

import exceptions.SerializationException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper static class, provides methods for working with file system.
 * Mostly wraps library methods, along with some sugaring and convenience
 * checks.
 */
public class Utils {
    public static void createDirectory(Path path) throws IOException {
        Files.createDirectories(path);
    }

    public static void createFile(Path path) throws IOException {
        if (Files.exists(path)) {
            return;
        }
        Files.createDirectories(path.getParent());
        Files.createFile(path);
    }

    public static void deleteDirectory(Path path) throws IOException {
        FileUtils.deleteDirectory(path.toFile());
    }

    public static List<Path> listAllFiles(Path root) throws IOException {
        return Files
                .walk(root)
                .skip(1)            // drop root-folder
                .filter(path ->     // skip .gut subfolder
                        !path.toString().contains(PathResolver.GUT_FOLDER_NAME))
                .collect(Collectors.toList());
    }

    public static void serialize(Object o, Path path) throws SerializationException {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(o);
        } catch (IOException e) {
            throw new SerializationException("Error during serialization: " + e.toString(), e);
        }
    }

    public static Object deserialize(Path path) throws SerializationException {
        try (ObjectInputStream oin =
                     new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return oin.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Error during deserialization: " + e.toString(), e);
        }
    }

    public static String getHash(Path path) throws IOException {
        if (Files.notExists(path)) {
            return null;
        }
        FileInputStream fis = new FileInputStream(path.toFile());
        return DigestUtils.md5Hex(fis);
    }

    public static void copyFile(Path source, Path destination) throws IOException {
        FileUtils.copyFile(source.toFile(), destination.toFile());
    }

    public static void delete(Path p) throws IOException {
        FileUtils.forceDelete(p.toFile());
    }
}
