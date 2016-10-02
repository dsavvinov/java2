package io.printing;

import gut.Difference;
import gut.FileObject;
import io.PathResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PrettyPrinter {
    public static void prettyPrintStagedChanges(Difference stageDiff, Logger log) {
        String indentation = "    ";
        log.println("Staged changes:\n");
        prettyPrintDifference(stageDiff, indentation, false, log);
    }

    public static void prettyPrintNotStagedChanges(Difference notStagedDiff, Logger log) {
        String indentation = "    ";
        log.println("Not staged changes:\n");
        prettyPrintDifference(notStagedDiff, indentation, true, log);
    }

    public static void prettyPrintDifference(
            Difference diff,
            String indentation,
            boolean isInWorkingTree,
            Logger log) {
        StringBuilder sb = new StringBuilder();
        for (FileObject f : diff.getFiles()) {
            // Get file status
            Difference.ModificationStatus status = diff.get(f);

            // Skip not modified files
            if (status == Difference.ModificationStatus.SAME) {
                continue;
            }

            // Get nice relative to user working dir paths (as git does)
            Path root = PathResolver.getRoot();
            Path workingDir = Paths.get(System.getProperty("user.dir"));
            Path absolutePath = root.resolve(f.getRelativePath());
            Path relativeToUser = workingDir.relativize(absolutePath);

            // Get nice status text
            String statusText;
            if (isInWorkingTree && status == Difference.ModificationStatus.ADDED) {
                statusText = "new";
            } else {
                statusText = status.toString().toLowerCase();
            }

            // So it looks like "    added: ../foo/bar"
            sb.append(indentation);
            sb.append(statusText);
            sb.append(": ");
            sb.append(relativeToUser.toString());
            sb.append("\n");
        }
        log.println(sb.toString());
    }
}
