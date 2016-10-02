package io.printing;

public class StandardOutputLogger implements Logger {
    @Override
    public void println(String message) {
        System.out.println(message);
    }

    @Override
    public void error(String message) {
        System.err.println(message);
    }
}
