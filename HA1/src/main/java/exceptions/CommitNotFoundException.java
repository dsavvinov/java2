package exceptions;

/**
 * Created by dsavvinov on 01.10.16.
 */
public class CommitNotFoundException extends Throwable {
    public CommitNotFoundException() {
        super();
    }

    public CommitNotFoundException(String message) {
        super(message);
    }
}
