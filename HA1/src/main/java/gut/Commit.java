package gut;

import java.io.Serializable;
import java.util.Date;

/**
 * Abstraction of commit in VCS
 * <p>
 * Holds pointer to the Snapshot of repository after this commit
 * and some additional information: date and message of commit and
 * commit-code (used for checkout)
 * <p>
 * Also, each (with some exceptions, see below) commit holds pointer to
 * the previous commit in history of repository.
 * <p>
 * After initialization of repository, dummy-commit will be created as
 * the root of whole tree of commits. This dummy-commit will never be shown
 * to the user and it is the only Commit object that legitimately has
 * it's previousCommit set to null.
 */
public class Commit implements Serializable {
    private final Snapshot snapshot;
    private final Date date;
    private final String code;
    private final String message;
    private Commit previousCommit;

    public Commit() {
        previousCommit = null;
        snapshot = new Snapshot();
        date = null;
        code = null;
        message = null;
    }

    public Commit(
            Snapshot curState,
            Date date,
            String code,
            String message
    ) {
        this.previousCommit = null;
        this.snapshot = curState;
        this.date = date;
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        if (code == null || date == null || message == null) {
            return "Empty commit";
        }
        return "Commit " + code + " made at " + date.toString() +
                "\n" +
                message;
    }

    public Commit getPreviousCommit() {
        return previousCommit;
    }

    public void setPreviousCommit(Commit previousCommit) {
        this.previousCommit = previousCommit;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

}
