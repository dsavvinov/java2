package exceptions;

public class InvalidProtocolException extends Exception {
    public InvalidProtocolException() {
        super();
    }

    public InvalidProtocolException(String message) {
        super(message);
    }

    public InvalidProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidProtocolException(Throwable cause) {
        super(cause);
    }
}
