package gut;


import io.PathResolver;
import io.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * Abstraction of revision of file in repository.
 *
 * Each {@see FileObject} can have many FileRevisions -
 * as much as different copies of it we have to store.
 * However, each FileRevision maps to the one and only one
 * FileObject.
 *
 */
public class FileRevision implements Serializable {
    private FileObject fileObject;

    // -1 stands for not versioned file (stored in index)
    private Integer revisionID;

    private String hash;

    public FileRevision(Path relativePath, Integer revisionID) throws IOException {
        this(new FileObject(relativePath), revisionID);
    }

    public FileRevision(FileObject file, Integer revisionID) throws IOException {
        this.fileObject = file;
        this.revisionID = revisionID;
        hash = Utils.getHash(PathResolver.getRoot().resolve(file.getRelativePath()));
    }

    public FileObject getFileObject() {
        return fileObject;
    }


    public String getHash() {
        return hash;
    }

    public Integer getRevisionID() {
        return revisionID;
    }
}
