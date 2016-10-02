package exceptions;

/**
 * Created by dsavvinov on 01.10.16.
 */
public class BranchAlreadyExistsException extends Throwable {
    public BranchAlreadyExistsException() {
        super();
    }

    public BranchAlreadyExistsException(String message) {
        super(message);
    }
}
