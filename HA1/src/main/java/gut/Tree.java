package gut;

import exceptions.BranchAlreadyExistsException;
import exceptions.BranchNotFoundException;
import io.PathResolver;
import exceptions.SerializationException;
import io.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

/**
 * Abstraction of tree of commits.
 *
 * Note that this class implements only high-level logic of tree commits.
 * It doesn't know how and where commits, branches are stored.
 *
 */
public class Tree implements Serializable {
    private final Commit root;
    private Map<String, Branch> branches;
    private Branch currentBranch;
    private Map<String, Commit> commits;
    private Tree() {
        root = new Commit();
        branches = new HashMap<>();
        commits = new HashMap<>();

        Branch origin = new Branch("origin", root);
        branches.put("origin", origin);
        currentBranch = origin;
    }

    public static Tree initInNewRepo() {
        return new Tree();
    }

    public static Tree loadFromExisting() throws SerializationException {
        Path treePath = PathResolver.getTree();
        return (Tree) Utils.deserialize(treePath);
    }

    public Commit getRoot() {
        return root;
    }

    public Map<String, Branch> getBranches() {
        return branches;
    }

    public Branch getCurrentBranch() {
        return currentBranch;
    }

    public List<Commit> getHistory() {
        List<Commit> history = new LinkedList<>();

        Commit current = currentBranch.getHead();
        while (current != root) {
            history.add(current);
            current = current.getPreviousCommit();
        }

        Collections.reverse(history);
        return history;
    }

    public void saveToDisk() throws SerializationException, IOException {
        Path treePath = PathResolver.getTree();
        Utils.createFile(treePath);
        Utils.serialize(this, treePath);
    }

    public void commitToCurrent(Commit newCommit) {
        // Link commits
        Commit curHead = currentBranch.getHead();
        newCommit.setPreviousCommit(curHead);

        currentBranch.setHead(newCommit);

        commits.put(newCommit.getCode(), newCommit);
    }

    public void createBranch(String branchName) throws BranchAlreadyExistsException {
        Branch newBranch = new Branch(branchName, currentBranch.getHead());
        if (branches.get(branchName) != null) {
            throw new BranchAlreadyExistsException("Branch " + branchName + " already exists");
        }
        branches.put(branchName, newBranch);
    }

    public void deleteBranch(String branchName) throws BranchNotFoundException {
        if (branches.get(branchName) == null) {
            throw new BranchNotFoundException("Branch " + branchName + " doesn't exist");
        }
        branches.put(branchName, null);
    }

    public void checkoutBranch(String branchName) throws BranchNotFoundException {
        Branch branch = branches.get(branchName);
        if (branch == null) {
            throw new BranchNotFoundException("Branch " + branchName + " doesn't exist");
        }
        currentBranch = branch;
    }

    public Commit findCommitByCode(String code) {
        return commits.get(code);
    }

    public Branch getBranchByName(String branchName) {
        return branches.get(branchName);
    }

}
