package gut;

import io.PathResolver;
import exceptions.SerializationException;
import io.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Abstraction of Local Stage Area (a.k.a "index" in Git)
 *
 */
public class LocalStageArea implements Serializable {
    private Snapshot state;

    public LocalStageArea() {
        state = new Snapshot();
    }

    public static LocalStageArea initInNewRepo() {
        return new LocalStageArea();
    }

    public static LocalStageArea loadFromExisting() throws SerializationException {
        Path lsaPath = PathResolver.getLsa();
        return (LocalStageArea) Utils.deserialize(lsaPath);
    }

    public Snapshot getState() {
        return state;
    }

    public void setState(Snapshot state) {
        this.state = state;
    }

    public void addToStage(String relPath) throws IOException {
        Path userDir = Paths.get(System.getProperty("user.dir"));
        Path absolutePath = userDir.resolve(relPath);
        Path relativeToRoot = PathResolver.getRoot().relativize(absolutePath);

        if (Files.notExists(absolutePath)) {
            throw new FileNotFoundException(relPath);
        }

        if (Files.isDirectory(absolutePath)) {
            throw new IOException("Recursive folder addition isn't supported yet");
        }

        state.add(relativeToRoot);
    }

    public void saveToDisk() throws SerializationException, IOException {
        Path lsaPath = PathResolver.getLsa();
        Utils.createFile(lsaPath);
        Utils.serialize(this, lsaPath);
    }

}
