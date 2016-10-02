package gut;

import io.PathResolver;
import io.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

public class Snapshot implements Serializable {
    public Snapshot(Map<FileObject, FileRevision> content) {
        this.content = content;
    }

    private Map<FileObject, FileRevision> content;

    public Snapshot() {
        content = new HashMap<>();
    }

    public Map<FileObject, FileRevision> getContent() {
        return content;
    }

    public FileRevision getFileRevision(FileObject key) {
        return content.get(key);
    }

    // Adds specified file, given by a relative to the root Path, to the Snapshot
    //
    // This method will try to avoid unnecessary copies of files by checking
    // if that revision already exists in storage - then, copy won't be performed,
    // and Snapshot will store just a pointer to already existing revision.
    public void add(Path relativeToRoot) throws IOException {
        // Prepare some vars
        Path absPath = PathResolver.getRoot().resolve(relativeToRoot);
        FileObject fileObject = new FileObject(relativeToRoot);
        String fileHash = Utils.getHash(absPath);

        // Check if we have already that revision of file in storage
        FileRevision fileRevision = LocalRepository.getRevision(fileHash);
        if (fileRevision != null) {
            content.put(fileObject, fileRevision);
            return;
        }

        // Otherwise, put that file to storage and get fresh revision
        fileRevision = LocalRepository.addToStorage(fileObject);

        // And save to the snapshot that fresh revision
        content.put(fileObject, fileRevision);
    }

    public boolean contains(FileObject fileObject) {
        return content.get(fileObject) != null;
    }

    public void updateFileRevision(FileObject file, FileRevision headRevision) {
        content.put(file, headRevision);
    }

    public void removeFile(FileObject file) {
        content.remove(file);
    }

    // Merge, taking only autoresolvable conflicts
    public Snapshot softMerge(Snapshot snapshotToMerge) {
        Difference mergeDiff = snapshotToMerge.getDiff(this);

        Snapshot newSnapshot = new Snapshot();
        for (Map.Entry<FileObject, Difference.ModificationStatus> entry
                : mergeDiff.getFileStatus().entrySet()) {
            FileObject file = entry.getKey();
            Difference.ModificationStatus status = entry.getValue();

            if (status.equals(Difference.ModificationStatus.MODIFIED)) {
                // TODO: check contents, make file-to-file diff, etc.
                // Currently just fail merge
                continue;
            }

            if (status.equals(Difference.ModificationStatus.DELETED)) {
                // TODO: check that auto-remove can be done
                // Currently just fail merge
                continue;
            }

            newSnapshot.updateFileRevision(file, snapshotToMerge.getFileRevision(file));
        }
        return newSnapshot;
    }

    // Merge, taking "their" revisions in case of conflict
    private Snapshot forceMerge(Snapshot snapshotToMerge) {
        // Essentially, it just equal to putting all entries from "their" snapshot
        // to our with overwriting.
        Map<FileObject, FileRevision> newContent = new HashMap<>(content);
        newContent.putAll(snapshotToMerge.getContent());
        return new Snapshot(content);
    }

    public List<FileObject> getConflicts(Snapshot snapshotToMerge) {
        Difference mergeDiff = snapshotToMerge.getDiff(this);
        List<FileObject> result = new ArrayList<>();

        for (Map.Entry<FileObject, Difference.ModificationStatus> entry
                : mergeDiff.getFileStatus().entrySet()) {
            FileObject file = entry.getKey();
            Difference.ModificationStatus status = entry.getValue();

            if (status.equals(Difference.ModificationStatus.MODIFIED)) {
                // TODO: check contents, make file-to-file diff, etc.
                // Currently just count as conflict
                result.add(file);
            }

            if (status.equals(Difference.ModificationStatus.DELETED)) {
                // TODO: check that auto-remove can be done
                // Currently just count as conflict
                result.add(file);
            }
        }

        return result;
    }

    public Difference getDiff(Snapshot target) {
        Difference result = new Difference();

        // First, build Maps FileObject -> this file FileRevision
        // for both snapshots.
        Map<FileObject, FileRevision> sourceFiles = this.getContent();
        Map<FileObject, FileRevision> targetFiles = target.getContent();

        // Build set of files, that are in the target snapshot,
        // but not in the source snapshot - those files was added.
        Set<FileObject> added = new HashSet<>(sourceFiles.keySet());
        added.removeAll(targetFiles.keySet());

        // Similar, files that are in the source snapshot,
        // but not in the target snapshot were deleted.
        Set<FileObject> deleted = new HashSet<>(targetFiles.keySet());
        deleted.removeAll(sourceFiles.keySet());

        // Modified and not modified ("Same") files are a bit more tricky.

        // First, get all files that exist both in the source and in the target.
        Set<FileObject> intersection = new HashSet<>(sourceFiles.keySet());
        intersection.retainAll(targetFiles.keySet());

        Set<FileObject> modified = new HashSet<>();
        Set<FileObject> same = new HashSet<>();

        // Now take each file in the intersection - this file exists
        // both in the source and in the target, but it could be modified.
        // So we carefully observe hash codes and leave file as not modified
        // only if they are equal.
        for (FileObject f : intersection) {
            FileRevision sourceRevision = sourceFiles.get(f);
            FileRevision targetRevision = targetFiles.get(f);
            if (sourceRevision.getHash().equals(targetRevision.getHash())) {
                same.add(f);
            } else {
                modified.add(f);
            }
        }

        // Finally, write all gathered information in resulting Difference
        result.updateAll(added, Difference.ModificationStatus.ADDED);
        result.updateAll(deleted, Difference.ModificationStatus.DELETED);
        result.updateAll(modified, Difference.ModificationStatus.MODIFIED);
        result.updateAll(same, Difference.ModificationStatus.SAME);
        result.setSource(this);
        result.setTarget(target);

        return result;
    }
}
