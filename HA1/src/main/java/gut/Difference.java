package gut;

import java.io.Serializable;
import java.util.*;

/**
 * Abstraction of difference between two Snapshots.
 * <p>
 * Difference tells us what should we do if we want
 * to transform {@link #source} {@link Snapshot} to
 * the {@link #target} {@link Snapshot}. To do so,
 * Difference stores Map from {@link FileObject} to
 * one of four {@link ModificationStatus}.
 */
public class Difference implements Serializable {
    private HashMap<FileObject, ModificationStatus> fileStatus;
    private Snapshot source;
    private Snapshot target;

    public Difference() {
        fileStatus = new HashMap<>();
        source = null;
        target = null;
    }

    public Difference(HashMap<FileObject, ModificationStatus> fileStatus) {
        this.fileStatus = fileStatus;
        source = null;
        target = null;
    }

    public void updateAll(Collection<FileObject> filesToAdd, ModificationStatus status) {
        filesToAdd.forEach(file -> update(file, status));
    }

    public void update(FileObject f, ModificationStatus status) {
        fileStatus.put(f, status);
    }

    // returns difference, which contains all changes,
    // that are in this-diff and not present in other-diff.
    //
    // If ModificationStatus is "Modified" then additional
    // check for hashes equality performed.
    //
    // Currently used for correctly evaluating not staged changes
    // ('other' is Stage-to-Head diff and 'this' is Index-To-Head diff)
    public Difference subtract(Difference other) {
        HashMap<FileObject, ModificationStatus> newStatuses = new HashMap<>(fileStatus);
        for (Map.Entry<FileObject, ModificationStatus> entry : fileStatus.entrySet()) {
            FileObject curFile = entry.getKey();
            ModificationStatus status = entry.getValue();

            ModificationStatus otherStatus = other.get(curFile);
            if (otherStatus == status) {
                if (status == ModificationStatus.DELETED) {
                    // if both differences mark current file as deleted,
                    // then this change shouldn't appear in the result.
                    //
                    // Note that we put "SAME" in newStatuses, indicating that
                    // for that file there are no *difference between differences*
                    newStatuses.put(curFile, ModificationStatus.SAME);
                    continue;
                }
                // check hashes
                String otherHash = other
                        .getSource()    // Get Snapshot of other Difference
                        .getFileRevision(entry.getKey())  // Get FileRevision for current fileObject in that Snapshot
                        .getHash();     // Get hash of current fileObject in that Snapshot

                String thisHash = getSource().getFileRevision(entry.getKey()).getHash();
                if (otherHash.equals(thisHash)) {
                    // Files are certainly the same; remove change from the result
                    newStatuses.put(curFile, ModificationStatus.SAME);
                } else {
                    // Otherwise, files are different - put "modified" status.
                    newStatuses.put(curFile, ModificationStatus.MODIFIED);
                }
            }
            // Statuses not equal - leave change;
        }

        return new Difference(newStatuses);
    }

    public HashMap<FileObject, ModificationStatus> getFileStatus() {
        return fileStatus;
    }

    public Snapshot getSource() {
        return source;
    }

    public void setSource(Snapshot source) {
        this.source = source;
    }

    public void setTarget(Snapshot target) {
        this.target = target;
    }

    public boolean contains(FileObject f) {
        return fileStatus.containsKey(f);
    }

    public ModificationStatus get(FileObject f) {
        return fileStatus.get(f);
    }

    public Set<FileObject> getFiles() {
        return fileStatus.keySet();
    }

    public enum ModificationStatus {
        ADDED,
        DELETED,
        MODIFIED,
        SAME
    }
}
