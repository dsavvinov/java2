package exceptions;

/**
 * Created by dsavvinov on 01.10.16.
 */
public class BranchNotFoundException extends Throwable {
    public BranchNotFoundException() {
        super();
    }

    public BranchNotFoundException(String message) {
        super(message);
    }
}
