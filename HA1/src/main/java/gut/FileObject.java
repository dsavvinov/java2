package gut;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Abstraction of file in the repository.
 * <p>
 * Note that FileObject <b>completely</b> neglects file's content!
 * It reflects only existence of some file in the system, leaving
 * {@link FileRevision} deal with different versions of that file.
 */
public class FileObject implements Serializable {
    private transient Path relativePath;

    public FileObject(Path relativePath) {
        this.relativePath = relativePath;
    }

    public Path getRelativePath() {
        return relativePath;
    }

    @Override
    public int hashCode() {
        return relativePath != null ? relativePath.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileObject) {
            FileObject fo = (FileObject) obj;
            return relativePath.equals(fo.relativePath);
        }
        return false;
    }

    /* We have to customize serialization protocol because Path doesn't
       implement Serializable interface.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(relativePath.toString());
    }

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String pathString = (String) in.readObject();
        relativePath = Paths.get(pathString);
    }
}
