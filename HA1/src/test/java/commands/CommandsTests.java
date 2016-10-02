package commands;

import io.printing.Logger;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * General idea: we will simulate some pretty long work-scenario,
 * and cover it with checks for correctness.
 * <p>
 * Details:
 * Let us define "step of scenario" as sequence of actions that executes
 * exactly 1 gut-command. Example of step: "Create test directory, then change working
 * directory to that dir, then call <gut init> here, then check that there were not any errors".
 * <p>
 * Each test corresponds to exactly one step. Each test calls previous one to restore state
 * of working tree and repository. At the end of test necessary checks are performed.
 * <p>
 * Scenario:
 * 1. Init repo in empty folder
 * 2. Create file in that folder and check that it marked as "new" in status
 * 3. Stage addition of that file
 * 4. Check that it marked as "added" in status
 * 5. Commit addition.
 * 6. Check that status now clean.
 * 7. Check that commit appears at the log.
 * 8. Create new branch, we will call it new_branch.
 * 9. Remove file and check that file was indeed removed from the working tree.
 * 10. Check that removal is staged.
 * 11. Checkout new_branch and check that file was restored in the working tree.
 * 12. Merge with origin, check that auto-merge unsucceeded and file1_our was generated
 * 13. Accept their changes by removing file.
 * 14. Merge again, check that now merge was succesfull.
 */

/*
    Я дико извиняюсь за адский код ниже, не успеваю написать нормально :(
 */

public class CommandsTests {
    private static Path curDir = Paths.get(System.getProperty("user.dir"));
    private static Path testDir = curDir.resolve("test_dir");

    static {
        System.setProperty("user.dir", testDir.toString());
        try {
            Files.createDirectory(testDir);
        } catch (Exception e) {
            fail();
        }
    }

    private LoggerMock log = new LoggerMock();

    @Test
    public void initNewRepo() throws Exception {
        Files.walk(testDir)
                .skip(1)
                .filter(it -> !it.toString().contains(".gut"))
                .forEach(it -> {
                    try {
                        Files.delete(it);
                    } catch (Exception ignored) {
                        fail();
                    }
                });

        String[] args = new String[0];
        new InitCommand().parseFrom(args, log).execute(log);


        assertTrue(log.errors.size() == 0);
    }

    @Test
    public void newFileTest() throws Exception {
        initNewRepo();
        log.clear();
        generateFile("file1", "rev1-file1");

        String[] args = new String[0];
        new StatusCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
        String lastMsg = log.printed.get(log.printed.size() - 1);
        assertTrue(lastMsg.contains("new: file1"));
    }

    @Test
    public void AddFileTest() throws Exception {
        newFileTest();
        log.clear();

        String[] args = {"-f", "file1"};
        new AddCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
    }

    @Test
    public void StatusAfterAdditionTest() throws Exception {
        AddFileTest();
        log.clear();

        String[] args = new String[0];
        new StatusCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
        assertTrue(log.contains("added: file1"));
    }

    @Test
    public void CommitTest() throws Exception {
        AddFileTest();
        log.clear();

        String[] args = {"-m", "added file1"};
        new CommitCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
    }

    @Test
    public void StatusAfterCommitTest() throws Exception {
        CommitTest();
        log.clear();

        String[] args = {};
        new StatusCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
        assertFalse(log.contains("file1"));
    }

    @Test
    public void LogAfterCommitTest() throws Exception {
        StatusAfterCommitTest();
        log.clear();

        String[] args = {};
        new LogCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
        assertTrue(log.contains("added file1"));
    }

    @Test
    public void MakeBranchTest() throws Exception {
        LogAfterCommitTest();
        log.clear();

        String[] args = {"-c", "new_branch"};
        new BranchCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
    }

    @Test
    public void RemoveFileTest() throws Exception {
        MakeBranchTest();
        log.clear();

        String[] args = {"-f", "file1"};
        new RemoveCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
        assertTrue(Files.notExists(testDir.resolve("file1")));
    }

    @Test
    public void StatusAfterRemovalTest() throws Exception {
        RemoveFileTest();
        log.clear();

        String[] args = {};
        new StatusCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
        assertTrue(log.contains("deleted: file1"));
    }

    @Test
    public void CommitRemovalTest() throws Exception {
        StatusAfterRemovalTest();
        log.clear();

        String[] args = {"-m", "file removed"};
        new CommitCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
    }

    @Test
    public void checkoutNewBranch() throws Exception {
        CommitRemovalTest();
        log.clear();

        String[] args = {"-b", "new_branch"};
        new CheckoutCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
        assertTrue(Files.exists(testDir.resolve("file1")));
    }

    @Test
    public void failMergeTest() throws Exception {
        checkoutNewBranch();
        log.clear();

        String[] args = {"-b", "origin", "-m", "Merge"};
        new MergeCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
        assertTrue(Files.notExists(testDir.resolve("file1")));
        assertTrue(Files.exists(testDir.resolve("file1.our")));
    }

    @Test
    public void prepareToMerge() throws Exception {
        failMergeTest();
        log.clear();

        String[] args = {"-f", "file1"};
        new RemoveCommand().parseFrom(args, log).execute(log);

        String[] args2 = {"-m", "preparing to merge"};
        new CommitCommand().parseFrom(args2, log).execute(log);

        Files.delete(testDir.resolve("file1.our"));

        assertTrue(log.errors.size() == 0);
    }

    @Test
    public void successfulMergeTest() throws Exception {
        prepareToMerge();
        log.clear();

        String[] args = {"-b", "origin", "-m", "Merge 2"};
        new MergeCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
        assertTrue(log.contains("auto-merging"));
    }

    @Test
    public void logAfterMergeTest() throws Exception {
        successfulMergeTest();
        log.clear();

        String[] args = {};
        new LogCommand().parseFrom(args, log).execute(log);

        assertTrue(log.errors.size() == 0);
        assertTrue(log.contains("Merge 2"));
    }

    private void generateFile(String fileName, String content) throws Exception {
        Path filePath = testDir.resolve(fileName);
        Files.write(filePath, Collections.singletonList(fileName), StandardCharsets.UTF_8);
    }

    public class LoggerMock implements Logger {
        public final ArrayList<String> printed = new ArrayList<>();
        public final ArrayList<String> errors = new ArrayList<>();

        @Override
        public void println(String message) {
            printed.add(message);
        }

        @Override
        public void error(String message) {
            errors.add(message);
        }

        public void clear() {
            printed.clear();
            errors.clear();
        }

        public boolean contains(String s) {
            return printed.stream().anyMatch(it -> it.contains(s));
        }
    }
}