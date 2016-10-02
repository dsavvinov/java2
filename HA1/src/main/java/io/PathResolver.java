package io;


import exceptions.RepoNotFoundException;
import gut.FileRevision;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helper static class, holds knowledge about file structure in the repository.
 *
 *
 * File structure:
 * .gut/
 *  |- objects/
 *  |   |- "file1 folder" (exact names are generated automatically)
 *  |   |   |- "revision 1 of file1"  (exact names are generated automatically)
 *  |   |   |- "revision 2 of file2"
 *  |   |       ...
 *  |   |- "file 2 folder"
 *  |       ...
 *  |- meta/
 *      |- tree        (dump of Tree object)
 *      |- lsa         (dump of LocalStageArea object)
 *      |- rev_manager (dump of RevisionManager object)
 */
public class PathResolver {
    public static final String GUT_FOLDER_NAME = ".gut";
    public static final String META_FOLDER_NAME = "meta";
    public static final String TREE_FILE_NAME = "tree";
    public static final String LSA_FILE_NAME = "lsa";
    public static final String REV_MANAGER_FILE_NAME = "rev_manager";
    public static final String STORAGE_FOLDER_NAME = "storage";

    private static Path root;
    private static Path gut;
    private static Path meta;
    private static Path tree;
    private static Path lsa;
    private static Path revisionManager;
    private static Path storage;

    public static void initHere() {
        initIn(Paths.get(System.getProperty("user.dir")));
    }

    public static void initIn(Path pathToRoot) {
        root = pathToRoot;

        gut = root.resolve(GUT_FOLDER_NAME);

        meta = gut.resolve(META_FOLDER_NAME);
        storage = gut.resolve(STORAGE_FOLDER_NAME);

        tree = meta.resolve(TREE_FILE_NAME);
        lsa = meta.resolve(LSA_FILE_NAME);
        revisionManager = meta.resolve(REV_MANAGER_FILE_NAME);
    }

    public static Path getRoot() {
        return root;
    }

    public static Path getGut() {
        return gut;
    }

    public static Path getTree() {
        return tree;
    }

    public static Path getMeta() {
        return meta;
    }

    public static Path getLsa() {
        return lsa;
    }


    // Travel from current dir to the root looking for the .gut-folder
    public static void findRepo() throws RepoNotFoundException {
        Path cur = Paths.get(System.getProperty("user.dir"));
        while (cur != null && Files.notExists(cur.resolve(GUT_FOLDER_NAME))) {
            cur = cur.getParent();
        }
        if (cur == null) {
            throw new RepoNotFoundException("Error: folder is not under .gut-control!");
        }
        initIn(cur);
    }

    public static Path getRevisionManager() {
        return revisionManager;
    }

    public static Path getPathToRevision(FileRevision revision) {
        String fileFolder = Integer.toString(
                revision
                        .getFileObject()
                        .getRelativePath()
                        .hashCode()
        );
        
        String revisionFile = revision.getRevisionID().toString();
        
        return storage.resolve(fileFolder).resolve(revisionFile);
    }

    public static Path getStorage() {
        return storage;
    }
}
