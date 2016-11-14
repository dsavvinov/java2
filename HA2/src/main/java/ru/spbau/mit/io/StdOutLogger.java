package ru.spbau.mit.io;

public class StdOutLogger implements Logger {
    @Override
    public synchronized void info(String message) {
        System.out.println(message);
    }

    @Override
    public synchronized void error(String message) {
        System.out.println(message);
    }

    @Override
    public synchronized void debug(String message) {
        System.out.println(message);
    }

    @Override
    public synchronized void trace(String message) {
        System.out.println(message);
    }
}
