package io;


import exceptions.SerializationException;
import gut.FileObject;
import gut.FileRevision;
import gut.Snapshot;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Incapsulates details of working with files versions.
 */
public class RevisionManager implements Serializable {
    // Hash -> FileRevision with that hash
    private Map<String, FileRevision> storage;

    // FileObject -> All revisions of that file
    private Map<FileObject, ArrayList<FileRevision>> fileRevisions;

    public RevisionManager() {
        storage = new HashMap<>();
        fileRevisions = new HashMap<>();
    }

    public static RevisionManager initInNewRepo() {
        return new RevisionManager();
    }

    public static RevisionManager loadFromExisting() throws SerializationException {
        Path rmPath = PathResolver.getRevisionManager();
        return (RevisionManager) Utils.deserialize(rmPath);
    }

    public FileRevision getRevision(String hash) {
        return storage.get(hash);
    }

    public void saveToDisk() throws SerializationException, IOException {
        Path rmPath = PathResolver.getRevisionManager();
        Utils.createFile(rmPath);
        Utils.serialize(this, rmPath);
    }

    public FileRevision addNewRevision(FileObject file) throws IOException {
        ArrayList<FileRevision> revisionsChain = fileRevisions.get(file);
        if (revisionsChain == null) {
            // If this is a first version of that file ever,
            // then create put in map new chain of revisions with
            // a single element - passed revision.
            FileRevision newRevision = new FileRevision(file, 0);

            revisionsChain = new ArrayList<>();
            revisionsChain.add(newRevision);

            fileRevisions.put(file, revisionsChain);

            return newRevision;
        }
        // Otherwise, append current revision with
        // revisionID = (last revision's ID) + 1
        Integer lastRevID = revisionsChain.get(revisionsChain.size() - 1).getRevisionID();
        FileRevision newRevision = new FileRevision(file, lastRevID + 1);

        revisionsChain.add(newRevision);

        return newRevision;
    }

    public void loadSnapshot(Snapshot ss) throws IOException {
        // Copy all files in the snapshot from the storage to the index
        for (Map.Entry<FileObject, FileRevision> entry : ss.getContent().entrySet()) {
            FileObject file = entry.getKey();
            FileRevision revision = entry.getValue();
            Path root = PathResolver.getRoot();

            Path placeInIndex = root.resolve(file.getRelativePath());
            Path placeInStorage = PathResolver.getPathToRevision(revision);

            Utils.copyFile(placeInStorage, placeInIndex);
        }
    }

    public void checkoutSnapshot(Snapshot ss) throws IOException {
        // Clean index
        List<Path> files = Utils.listAllFiles(PathResolver.getRoot());
        for (Path p : files) {
            // We don't want to care much about order of deletion,
            // so we have to always check if we deleted this path earlier
            if (Files.notExists(p)) {
                continue;
            }

            Utils.delete(p);
        }

        loadSnapshot(ss);
    }
}
