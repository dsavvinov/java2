package exceptions;

public class DownloadingException extends Exception {
    public DownloadingException() {
        super();
    }

    public DownloadingException(String message) {
        super(message);
    }

    public DownloadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DownloadingException(Throwable cause) {
        super(cause);
    }

    protected DownloadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
