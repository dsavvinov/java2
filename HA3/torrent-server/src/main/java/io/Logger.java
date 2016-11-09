package io;

public interface Logger {
    void info(String message);

    void error(String message);

    void trace(String message);

    void debug(String message);
}
