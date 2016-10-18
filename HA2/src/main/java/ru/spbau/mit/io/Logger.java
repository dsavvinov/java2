package ru.spbau.mit.io;

public interface Logger {
    void info(String message);
    void error(String message);
    void debug(String message);
    void trace(String message);
}
