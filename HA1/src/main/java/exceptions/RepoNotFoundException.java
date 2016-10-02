package exceptions;

/**
 * Created by dsavvinov on 29.09.16.
 */
public class RepoNotFoundException extends Throwable {
    public RepoNotFoundException() {
        super();
    }

    public RepoNotFoundException(String message) {
        super(message);
    }

    public RepoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepoNotFoundException(Throwable cause) {
        super(cause);
    }

    protected RepoNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
