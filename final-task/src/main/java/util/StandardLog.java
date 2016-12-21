package util;

public class StandardLog implements Log {

    @Override
    public void info(String message) {
        System.out.println(message);
    }

    @Override
    public void trace(String message) {
        System.out.println(message);
    }

    @Override
    public void error(String message) {
        System.err.println(message);
    }
}
