package gut;

import exceptions.*;
import io.*;
import io.printing.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Abstraction of the whole local repository.
 * <p>
 * LocalRepository holds references to all persistent objects and
 * incapsulates details of storing and loading those objects.
 * <p>
 * Also, LocalRepository does dirty IO work, improving incapsulation
 * of inner logic classes (e.g. {@link Tree#checkoutBranch } doesn't do anything
 * in the file system, because @{link Tree} shouldn't know anything about how and where
 * objects are stored)
 */

/*
    Этот класс выглядит очень большим и громоздким, т.к. в нем мы вынуждены написать
    по >= 1 методу на каждую команду + еще некоторое количество служебных методов.

    Альтернативный дизайн - заставить команды ходить напрямую к классам, которые им нужны
    (например, commit/checkout - к RevisionManager'у, Tree, LocalStageArea и т.д.)

    В этом тоже есть свои минусы, т.к. ухудшается инкапсуляция деталей реализации репозитория,
    все классы-комманды тут же узнают о существовании всех сервисных классов, и все изменения
    в структуре репозитория будут сказываться на классах-командах, в то время как хотелось, чтобы
    они были глупыми обертками, дергающими 1-2 нужных метода.

    Я решил остановиться на такой реализации не в последнюю очередь потому, что изначально написал
    так, а стоит ли рефакторить - не понятно. Было бы интересно услышать твое мнение на этот счет,
    просто на будущее.
 */

public class LocalRepository {
    private static Tree tree;
    private static LocalStageArea lsa;
    private static RevisionManager revisionManager;

    /**
     * Initializes new .gut-repository in current working directory.
     * <p>
     * Any existing .gut-folder will be removed.
     *
     * @throws IOException
     * @throws SerializationException
     */
    public static void createNew() throws IOException, SerializationException {
        PathResolver.initHere();

        // Prepare folder-structure
        Path gut = PathResolver.getGut();
        if (Files.exists(gut)) {
            Utils.deleteDirectory(gut);
        }
        Utils.createDirectory(gut);

        Path meta = PathResolver.getMeta();
        Utils.createDirectory(meta);

        Path storage = PathResolver.getStorage();
        Utils.createDirectory(storage);

        // Init meta-objects
        tree = Tree.initInNewRepo();
        lsa = LocalStageArea.initInNewRepo();
        revisionManager = RevisionManager.initInNewRepo();
    }

    /**
     * Loads .gut-repository that control this folder.
     * <p>
     * This is done by walking from the current directory up
     * to the file system root, looking for .gut folder.
     * <p>
     * If .gut folder is found, then directory that contains it
     * is saved as root of repository.
     * Then all meta-structures from .gut-folder will be loaded in memory.
     * <p>
     * If .gut folder is not found, then {@link SerializationException} is thrown.
     *
     * @throws RepoNotFoundException
     * @throws SerializationException
     */
    public static void tryLoad()
            throws RepoNotFoundException, SerializationException {
        PathResolver.findRepo();

        tree = Tree.loadFromExisting();
        lsa = LocalStageArea.loadFromExisting();
        revisionManager = RevisionManager.loadFromExisting();
    }

    /**
     * Deserializes all meta-structures to the .gut folder
     *
     * @throws IOException
     * @throws SerializationException
     */
    public static void saveToDisk() throws IOException, SerializationException {
        tree.saveToDisk();
        lsa.saveToDisk();
        revisionManager.saveToDisk();
    }

    /**
     * Gets difference from the working tree to the current HEAD
     * <p>
     * See {@link Difference} for more details.
     *
     * @return
     * @throws IOException
     */
    public static Difference getDiffIndexToHead() throws IOException {
        Snapshot headSnapshot = tree.getCurrentBranch().getHead().getSnapshot();
        Snapshot indexSnapshot = buildIndexSnapshot();
        return indexSnapshot.getDiff(headSnapshot);
    }

    /**
     * Gets difference from {@param source} to the current HEAD
     * <p>
     * See {@link Difference} for more details
     *
     * @param source
     * @return
     * @throws IOException
     */
    public static Difference getDiffToHead(Snapshot source) throws IOException {
        Snapshot headSnapshot = tree.getCurrentBranch().getHead().getSnapshot();
        return source.getDiff(headSnapshot);
    }

    /**
     * Builds working tree snapshot.
     * <p>
     * See {@link Snapshot} for more details.
     *
     * @return
     * @throws IOException
     */
    private static Snapshot buildIndexSnapshot() throws IOException {
        Path root = PathResolver.getRoot();
        List<Path> files = Utils.listAllFiles(root);
        Map<FileObject, FileRevision> fileRevisions = new HashMap<>();

        for (Path absPath : files) {
            Path relPath = root.relativize(absPath);
            FileObject fileObject = new FileObject(relPath);

            String hash = Utils.getHash(absPath);

            FileRevision fr = revisionManager.getRevision(hash);

            if (fr == null) {
                // If this file doesn't have a revision, place it
                // with a special -1 code (indicating that this revision
                // exists only in index)
                fileRevisions.put(fileObject, new FileRevision(relPath, -1));
            } else {
                // Otherwise just use this revision in a snapshot
                fileRevisions.put(fileObject, fr);
            }
        }
        return new Snapshot(fileRevisions);
    }

    /**
     * Adds specified file to the Local Stage Area and stores
     * it revision in the storage.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static FileRevision addToStorage(FileObject file) throws IOException {
        // Build a new FileRevision object
        FileRevision newRevision = revisionManager.addNewRevision(file);

        // Now copy file to the storage
        Path root = PathResolver.getRoot();
        Path absPath = root.resolve(file.getRelativePath());
        Path placeInStorage = PathResolver.getPathToRevision(newRevision);
        Utils.copyFile(absPath, placeInStorage);

        return newRevision;
    }

    /**
     * Creates Commit from the current Snapshot of Local Stage Area
     * and saves it to the Commits Tree.
     *
     * @param message
     */
    public static void commitStage(String message) {
        Date curDate = new Date();
        Commit newCommit = new Commit(
                /* previousCommit = null */
                /* nextCommit = null */
                /* state = */ lsa.getState(),
                /* date = */ curDate,
                /* code = */ Integer.toString(curDate.hashCode()),
                /* message = */ message
        );

        tree.commitToCurrent(newCommit);
    }

    /**
     * Creates branch with specified {@param branchName}.
     * <p>
     * HEAD of the new branch will point to the current branch HEAD.
     *
     * @param branchName
     * @throws BranchAlreadyExistsException
     */
    public static void createBranch(String branchName) throws BranchAlreadyExistsException {
        tree.createBranch(branchName);
    }

    /**
     * Deletes branch with specified {@param branchName}
     *
     * @param branchName
     * @throws BranchNotFoundException
     */
    public static void deleteBranch(String branchName) throws BranchNotFoundException {
        tree.deleteBranch(branchName);
    }

    /**
     * Checkouts branch with specified {@param branchName}
     * <p>
     * Note that all working tree along with any uncomitted changes
     * will be overwritten by the {@param branchName} HEAD's Snapshot.
     *
     * @param branchName
     * @throws IOException
     * @throws BranchNotFoundException
     */
    public static void checkoutBranch(String branchName)
            throws IOException, BranchNotFoundException {
        // Make changes in Tree-abstraction
        tree.checkoutBranch(branchName);

        // Load snapshot in the index
        Snapshot headSnapshot = tree.getCurrentBranch().getHead().getSnapshot();
        revisionManager.checkoutSnapshot(headSnapshot);

        // Don't forget to update local stage area
        // (by default empty after checkout i.e. it's snapshot = head's snapshot)
        lsa.setState(headSnapshot);
    }

    /**
     * Checkouts commit with the specified {@param revCode}
     *
     * @param revCode
     * @throws CommitNotFoundException
     * @throws IOException
     */
    public static void checkoutCommit(String revCode) throws CommitNotFoundException, IOException {
        Commit commitToCheckout = tree.findCommitByCode(revCode);
        if (commitToCheckout == null) {
            throw new CommitNotFoundException("Commit with code <" + revCode + "> not found");
        }

        // Load snapshot in the index
        Snapshot commitSnapshot = commitToCheckout.getSnapshot();
        revisionManager.checkoutSnapshot(commitSnapshot);

        // Update LSA
        Snapshot headSnapshot = tree.getCurrentBranch().getHead().getSnapshot();
        lsa.setState(headSnapshot);
    }

    /**
     * Removes all not-versioned files from the working tree.
     *
     * @throws IOException
     */
    public static void clean() throws IOException {
        Path root = PathResolver.getRoot();
        List<Path> files = Utils.listAllFiles(root);

        Snapshot headSnapshot = tree.getCurrentBranch().getHead().getSnapshot();
        Snapshot lsaSnapshot = lsa.getState();

        for (Path f : files) {
            FileObject fo = new FileObject(root.relativize(f));
            if (headSnapshot.contains(fo) || lsaSnapshot.contains(fo)) {
                continue;
            }
            Utils.delete(f);
        }
    }

    /**
     * Cancels staged changes to the specified by the {@param relPath} file.
     * <p>
     * Essentially, it just loads revision of that file from current branch HEAD
     * into Local Stage Area
     *
     * @param relPath
     * @throws FileNotVersionedException
     */
    public static void reset(String relPath) throws FileNotVersionedException {
        // Convert relative-to-user-dir path to relative-to-root
        Path userDir = Paths.get(System.getProperty("user.dir"));
        Path absolutePath = userDir.resolve(relPath);
        Path root = PathResolver.getRoot();
        Path relativeToRoot = root.relativize(absolutePath);

        FileObject file = new FileObject(relativeToRoot);

        // Now we should take revision of that file from the HEAD
        // and put that revision in the local stage area
        Snapshot headSnapshot = tree.getCurrentBranch().getHead().getSnapshot();
        FileRevision headRevision = headSnapshot.getFileRevision(file);
        if (headRevision == null) {
            throw new FileNotVersionedException(
                    "File " + absolutePath + " not versioned, can't reset");
        }

        Snapshot lsaSnapshot = lsa.getState();
        lsaSnapshot.updateFileRevision(file, headRevision);
    }

    /**
     * Removes specified by the {@param relPath} file from working tree
     * and stages it's removal from the local repository.
     *
     * @param relPath
     * @throws IOException
     * @throws FileNotVersionedException
     */
    public static void remove(String relPath) throws IOException, FileNotVersionedException {
        // Convert relative-to-user-dir path to relative-to-root
        Path userDir = Paths.get(System.getProperty("user.dir"));
        Path absolutePath = userDir.resolve(relPath);
        Path root = PathResolver.getRoot();
        Path relativeToRoot = root.relativize(absolutePath);

        FileObject file = new FileObject(relativeToRoot);

        // First, stage file removal from storage
        Snapshot stageSnapshot = lsa.getState();
        if (stageSnapshot.getFileRevision(file) == null) {
            throw new FileNotVersionedException("File " + absolutePath + " not versioned");
        }
        stageSnapshot.removeFile(file);

        // Then, remove file from working tree, if it exists
        if (Files.exists(absolutePath)) {
            Utils.delete(absolutePath);
        }
    }

    public static Tree getTree() {
        return tree;
    }

    public static LocalStageArea getLsa() {
        return lsa;
    }

    public static FileRevision getRevision(String fileHash) {
        return revisionManager.getRevision(fileHash);
    }


    // Pretty dirty, messy and stupid merge.
    // TODO: rewrite to the proper file-to-file difference
    public static void mergeWith(String branchName, String message, Logger log)
            throws BranchNotFoundException, IOException {
        Branch branchToMerge = tree.getBranchByName(branchName);
        if (branchToMerge == null) {
            throw new BranchNotFoundException("Branch " + branchName + " not found");
        }

        Snapshot currentSnapshot = tree.getCurrentBranch().getHead().getSnapshot();
        Snapshot snapshotToMerge = branchToMerge.getHead().getSnapshot();

        List<FileObject> conflicts = currentSnapshot.getConflicts(snapshotToMerge);
        Snapshot newState = currentSnapshot.softMerge(snapshotToMerge);

        if (conflicts.isEmpty()) {
            // If there are no conflicts then just create merge-commit with newState
            log.println("No conflicts are found, auto-merging...");
            lsa.setState(newState);
            commitStage(message);
            return;
        }
        log.println("Conflicts are found. Instead of each conflict 2 files placed in the working dir:\n"
                + "<file-name>_ours and <file-name>_their representing two conflicting versions\n"
                + "One of this two files can be absent, indicating that in corresponding branch this file was removed");

        // Load autoresolvable changes
        revisionManager.loadSnapshot(newState);
        lsa.setState(tree.getCurrentBranch().getHead().getSnapshot());

        // Now the structure is following:
        //      - working tree contains all resolvable changes + conflicted revision from working tree
        //      - local stage area contains ONLY resolvable changes
        // So, now we have to rename conflicted revisions in working tree and place corresponding revisions
        // from other snapshot
        generateHelpers(conflicts, currentSnapshot, snapshotToMerge);
    }

    private static void generateHelpers(List<FileObject> conflicts, Snapshot our, Snapshot their) throws IOException {
        for (FileObject file : conflicts) {
            Path root = PathResolver.getRoot();
            Path absolutePath = root.resolve(file.getRelativePath());
            Path fileFolder = absolutePath.getParent();
            String fileName = absolutePath.getFileName().toString();

            // 1. Rename working tree revision of conflict to <file-name>.our
            if (Files.exists(absolutePath)) {
                String newFileName = fileName + ".our";
                Path targetPath = fileFolder.resolve(newFileName);
                Files.move(absolutePath, targetPath);
            }

            // 2. Copy to the working tree "their" revision of conflict as <file-name>.their
            FileRevision theirRevision = their.getFileRevision(file);
            if (theirRevision != null) {
                String newFileName = fileName + ".their";
                Path targetPath = fileFolder.resolve(newFileName);
                Path theirSource = PathResolver.getPathToRevision(theirRevision);
                Utils.copyFile(theirSource, targetPath);
            }
        }
    }
}
