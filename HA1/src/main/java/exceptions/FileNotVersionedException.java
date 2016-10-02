package exceptions;

public class FileNotVersionedException extends Throwable {
    public FileNotVersionedException() {
        super();
    }

    public FileNotVersionedException(String message) {
        super(message);
    }
}
